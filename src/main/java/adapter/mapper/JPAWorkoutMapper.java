package adapter.mapper;

import adapter.out.Entities.ExerciseEntity;
import adapter.out.Entities.WorkoutEntity;
import domain.model.Workout;

public class JPAWorkoutMapper {

    public static Workout toDomain(WorkoutEntity workoutEntity) {
        return new Workout(workoutEntity.getId(), workoutEntity.getName(), workoutEntity.getDescription(), workoutEntity.getCreatedAt(),
                workoutEntity.getExercises().stream().map(ExerciseEntity::getId).toList(), workoutEntity.getCreatedBy().getId());
    }

    public static WorkoutEntity toEntity(Workout workout) {
        return new WorkoutEntity(workout.getId(), workout.getName(), workout.getDescription(), workout.getCreatedAt());
    }
}