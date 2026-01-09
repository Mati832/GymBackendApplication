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
}