package adapter.mapper;

import adapter.out.Entities.ExerciseEntity;
import adapter.out.Entities.WorkoutEntity;
import domain.model.Workout;

import java.util.ArrayList;

public class JPAWorkoutMapper {

    public static Workout toDomain(WorkoutEntity workoutEntity) {
        return new Workout(workoutEntity.getId(), workoutEntity.getName(), workoutEntity.getDescription(), workoutEntity.getCreatedAt(),
                new ArrayList<>(workoutEntity.getExercises().stream().map(ExerciseEntity::getId).toList()), workoutEntity.getOwner().getId());
    }

    public static WorkoutEntity toEntity(Workout workout) {
        return new WorkoutEntity(workout.getId(), workout.getName(), workout.getDescription(), workout.getCreatedAt());
    }

    public static WorkoutEntity copyFromDB(WorkoutEntity workoutEntity) {
        WorkoutEntity workoutEntityCopy = new WorkoutEntity();
        workoutEntityCopy.setName(workoutEntity.getName());
        workoutEntityCopy.setDescription(workoutEntity.getDescription());
        workoutEntityCopy.setCreatedAt(workoutEntity.getCreatedAt());
        workoutEntityCopy.setExercises(workoutEntity.getExercises().stream().map(eEntity -> {
            ExerciseEntity exerciseEntity = JPAExerciseMapper.copyFromDB(eEntity);
            exerciseEntity.setWorkout(workoutEntityCopy);
            return exerciseEntity;
        }).toList());
        workoutEntityCopy.setOwner(workoutEntity.getOwner());
        return workoutEntityCopy;
    }
}