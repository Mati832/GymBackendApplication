package adapter.out;

import adapter.mapper.JPAWorkoutMapper;
import adapter.out.Entities.ExerciseEntity;
import adapter.out.Entities.UserEntity;
import adapter.out.Entities.WorkoutEntity;
import application.commands.exercise.ExerciseFilter;
import application.port.out.WorkoutPorts.*;
import application.commands.workout.WorkoutFilter;
import domain.exceptions.ExerciseNotFoundException;
import domain.exceptions.UserNotFoundException;
import domain.model.Workout;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

import static adapter.mapper.JPAWorkoutMapper.toDomain;

@ApplicationScoped
public class JPAWorkoutAdapter implements FindWorkoutByIdPort, LoadWorkoutByIdPort, LoadWorkouts, CountWorkouts, SaveWorkoutPort,
        UpdateWorkoutPort, DeleteWorkoutPort {
    @Inject
    EntityManager em;

    @Override
    //Only used internally. Loads every exercise
    public Workout findWorkoutById(Long workoutId) {
        WorkoutEntity workoutEntity = em.find(WorkoutEntity.class, workoutId);
        return workoutEntity == null ? null : toDomain(workoutEntity);
    }

    @Override
    //used for get request. Does not load every exercise
    public Workout laodWorkout(Long workoutId) {
        WorkoutEntity workoutEntity = em.find(WorkoutEntity.class, workoutId);
        if(workoutEntity == null) return null;
        return slimMapper(workoutEntity);
    }

    @Override
    //for pagination
    public List<Workout> loadWorkouts(WorkoutFilter filter, int page, int size) {
        return buildQuery(filter, WorkoutEntity.class, false)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList()
                .stream()
                .map(this::slimMapper)
                .toList();
    }

    @Override
    //needed to calculate the amount of pages for pagination
    public int countWorkouts(WorkoutFilter filter) {
        return buildQuery(filter, int.class, true).getSingleResult();
    }

    @Override
    @Transactional
    public Workout saveWorkout(Workout workout) {
        WorkoutEntity workoutEntity = toEntity(workout);
        em.persist(workoutEntity);
        return toDomain(workoutEntity);
    }

    @Override
    @Transactional
    public Workout update(Workout workout) {
        WorkoutEntity workoutEntity = em.find(WorkoutEntity.class, workout.getId());
        if (workoutEntity == null) return null;
        workoutEntity.setId(workout.getId());
        workoutEntity.setName(workout.getName());
        workoutEntity.setDescription(workout.getDescription());
        workoutEntity.setCreatedAt(workout.getCreatedAt());
        workoutEntity.getExercises().clear();
        workoutEntity.getExercises().addAll(workout.getExercises().stream().map(eId -> em.find(ExerciseEntity.class, eId)).toList());
        workoutEntity.setOwner(em.find(UserEntity.class, workout.getCreatedByUserId()));
        return toDomain(workoutEntity);
    }

    @Override
    @Transactional
    public void deleteWorkout(Long workoutId) {
        WorkoutEntity workoutEntity = em.find(WorkoutEntity.class, workoutId);
        if(workoutEntity == null) return;
        workoutEntity.getOwner().getWorkouts().remove(workoutEntity);
        em.remove(workoutEntity);
    }

    private WorkoutEntity toEntity(Workout workout){
        WorkoutEntity workoutEntity = JPAWorkoutMapper.toEntity(workout);
        workoutEntity.setExercises(new ArrayList<>(workout.getExercises().stream().map(eId -> {
            ExerciseEntity eEntity = em.find(ExerciseEntity.class, eId);
            if(eEntity == null) throw new ExerciseNotFoundException("exercise not found: " + eId);
            return  eEntity;
        }).toList()));
        if(workout.getCreatedByUserId() == null) return  workoutEntity;
        UserEntity userEntity = em.find(UserEntity.class, workout.getCreatedByUserId());
        if(userEntity == null) throw new UserNotFoundException("user not found: " + workout.getCreatedByUserId());
        workoutEntity.setOwner(userEntity);
        return  workoutEntity;
    }

    private Workout slimMapper(WorkoutEntity workoutEntity) {
        return new Workout(
                workoutEntity.getId(),
                workoutEntity.getName(),
                workoutEntity.getDescription(),
                workoutEntity.getCreatedAt(),
                workoutEntity.getOwner().getId()
        );
    }
    private <T> TypedQuery<T> buildQuery(WorkoutFilter filter, Class<T> resultClass, boolean isCount) {
        String selectPart = isCount ? "SELECT COUNT(w) " : "SELECT w ";
        StringBuilder queryString = new StringBuilder(selectPart);
        queryString.append("FROM WorkoutEntity w WHERE 1=1 ");

        if (filter.userId() != null) queryString.append("AND w.owner.id = :userId ");
        if (filter.name() != null) queryString.append("AND lower(w.name) LIKE lower(:workoutName) ");
        if (filter.createdAfter() != null) queryString.append("AND w.createdAt >= :createdAfter ");
        if (filter.createdBefore() != null) queryString.append("AND w.createdAt <= :createdBefore ");

        TypedQuery<T> query = em.createQuery(queryString.toString(), resultClass);

        if (filter.userId() != null) query.setParameter("userId", filter.userId());
        if (filter.name() != null) query.setParameter("workoutName", "%" + filter.name() + "%");
        if (filter.createdAfter() != null) query.setParameter("createdAfter", filter.createdAfter());
        if (filter.createdBefore() != null) query.setParameter("createdBefore", filter.createdBefore());

        return query;
    }
}