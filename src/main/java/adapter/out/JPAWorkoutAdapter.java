package adapter.out;

import adapter.mapper.JPAWorkoutMapper;
import adapter.out.Entities.ExerciseEntity;
import adapter.out.Entities.UserEntity;
import adapter.out.Entities.WorkoutEntity;
import application.port.out.WorkoutPorts.DeleteWorkoutPort;
import application.port.out.WorkoutPorts.FindWorkoutByIdPort;
import application.port.out.WorkoutPorts.SaveWorkoutPort;
import application.port.out.WorkoutPorts.UpdateWorkoutPort;
import domain.exceptions.ExerciseNotFoundException;
import domain.exceptions.UserNotFoundException;
import domain.exceptions.WorkoutNotFoundException;
import domain.model.Workout;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.ArrayList;

import static adapter.mapper.JPAWorkoutMapper.toDomain;

@ApplicationScoped
public class JPAWorkoutAdapter implements FindWorkoutByIdPort, SaveWorkoutPort, UpdateWorkoutPort, DeleteWorkoutPort {
    @Inject
    EntityManager em;

    @Override
    public Workout findWorkoutById(Long workoutId) {
        WorkoutEntity workoutEntity = em.find(WorkoutEntity.class, workoutId);
        return workoutEntity == null ? null : toDomain(workoutEntity);
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
}