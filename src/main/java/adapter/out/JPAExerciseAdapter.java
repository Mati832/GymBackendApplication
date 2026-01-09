package adapter.out;

import adapter.mapper.JPAExerciseMapper;
import adapter.out.Entities.ExerciseEntity;
import adapter.out.Entities.ExerciseSetEntity;
import adapter.out.Entities.UserEntity;
import adapter.out.Entities.WorkoutEntity;
import application.commands.exercise.ExerciseFilter;
import application.port.out.ExercisePorts.*;
import domain.exceptions.ExerciseSetNotFoundException;
import domain.model.Exercise;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

import static adapter.mapper.JPAExerciseMapper.toDomain;

@ApplicationScoped
public class JPAExerciseAdapter implements LoadExerciseByIdPort, LoadExercisesPort, CountExercisesPort, FindExerciseByIdPort,
        SaveExercisePort, UpdateExercisePort, DeleteExercisePort {
    @Inject
    EntityManager em;

    @Override
    public Exercise loadExerciseById(Long exerciseId){
        ExerciseEntity entity = em.find(ExerciseEntity.class, exerciseId);
        if(entity == null) return null;
        return slimMapper(entity);
    }

    @Override
    public List<Exercise> loadExercises(ExerciseFilter filter, int page, int size){
        return buildQuery(filter, ExerciseEntity.class, false)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList()
                .stream()
                .map(this::slimMapper)
                .toList();
    }


    @Override
    public int countExercises(ExerciseFilter filter) {
        return buildQuery(filter, int.class, true)
                .getSingleResult();
    }


    @Override
    public Exercise findExerciseById(Long exerciseId) {
        ExerciseEntity exerciseEntity = em.find(ExerciseEntity.class, exerciseId);
        return exerciseEntity == null ? null : toDomain(exerciseEntity);
    }

    @Override
    @Transactional
    public Exercise saveExercise(Exercise exercise) {
        ExerciseEntity exerciseEntity = toEntity(exercise);
        em.persist(exerciseEntity);
        return toDomain(exerciseEntity);
    }

    @Override
    @Transactional
    public Exercise updateExercise(Exercise exercise) {
        ExerciseEntity exerciseEntity = em.find(ExerciseEntity.class, exercise.getId());
        if(exerciseEntity == null) return null;
        exerciseEntity.setId(exercise.getId());
        exerciseEntity.setName(exercise.getName());
        exerciseEntity.setType(exercise.getType());
        exerciseEntity.setDurationInSec(exercise.getDurationInSec());
        exerciseEntity.setOwner(em.find(UserEntity.class, exercise.getCreatedByUserId()));
        exerciseEntity.getExerciseSets().clear();
        exerciseEntity.getExerciseSets().addAll(exercise.getExerciseSets().stream()
                .map(eSetId -> em.find(ExerciseSetEntity.class, eSetId)).toList());
        if(exercise.getWorkoutId() != null)
            exerciseEntity.setWorkout(em.find(WorkoutEntity.class, exercise.getWorkoutId()));
        return toDomain(exerciseEntity);
    }

    @Override
    @Transactional
    public void deleteExercise(Long exerciseId) {
        ExerciseEntity exerciseEntity = em.find(ExerciseEntity.class, exerciseId);
        if(exerciseEntity == null) return;

        exerciseEntity.getOwner().getExercises().remove(exerciseEntity);
        if(exerciseEntity.getWorkout() != null)
         exerciseEntity.getWorkout().getExercises().remove(exerciseEntity);
        em.remove(exerciseEntity);
    }

    private ExerciseEntity toEntity(Exercise exercise) {
        ExerciseEntity exerciseEntity = JPAExerciseMapper.toEntity(exercise);
        exerciseEntity.setExerciseSets(new ArrayList<>(exercise.getExerciseSets().stream().map(eSetId -> {
            ExerciseSetEntity eSetEntity = em.find(ExerciseSetEntity.class, eSetId);
            if (eSetEntity == null) throw new ExerciseSetNotFoundException("exerciseSet not found: " + eSetId);
            return  eSetEntity;
        }).toList()));
        if(exercise.getCreatedByUserId() != null) exerciseEntity.setOwner(em.find(UserEntity.class, exercise.getCreatedByUserId()));
        if(exercise.getWorkoutId() != null) exerciseEntity.setWorkout(em.find(WorkoutEntity.class, exercise.getWorkoutId()));
        return  exerciseEntity;
    }

    private Exercise slimMapper(ExerciseEntity exerciseEntity) {
        return new Exercise(
                exerciseEntity.getId(),
                exerciseEntity.getName(),
                exerciseEntity.getType(),
                exerciseEntity.getDurationInSec(),
                exerciseEntity.getOwner().getId(),
                exerciseEntity.getWorkout().getId()
        );
    }

    private <T> TypedQuery<T> buildQuery(ExerciseFilter filter, Class<T> resultClass, boolean isCount) {
        String selectPart = isCount ? "SELECT COUNT(e) " : "SELECT e ";
        StringBuilder queryString = new StringBuilder(selectPart);
        queryString.append("FROM ExerciseEntity e WHERE 1=1 ");

        if (filter.userId() != null) queryString.append("AND e.owner.id = :userId ");
        if (filter.workoutId() != null) queryString.append("AND e.workout.id = :workoutId ");
        if (filter.name() != null) queryString.append("AND lower(e.name) LIKE lower(:exerciseName) ");
        if (filter.createdAfter() != null) queryString.append("AND e.createdAt >= :createdAfter ");
        if (filter.createdBefore() != null) queryString.append("AND e.createdAt <= :createdBefore ");

        TypedQuery<T> query = em.createQuery(queryString.toString(), resultClass);

        if (filter.userId() != null) query.setParameter("userId", filter.userId());
        if (filter.workoutId() != null) query.setParameter("workoutId", filter.workoutId());
        if (filter.name() != null) query.setParameter("exerciseName", "%" + filter.name() + "%");
        if (filter.createdAfter() != null) query.setParameter("createdAfter", filter.createdAfter());
        if (filter.createdBefore() != null) query.setParameter("createdBefore", filter.createdBefore());

        return query;
    }
}