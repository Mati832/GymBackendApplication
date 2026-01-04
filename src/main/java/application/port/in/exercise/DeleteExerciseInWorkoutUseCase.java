package application.port.in.exercise;

import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.Workout;

@FunctionalInterface
public interface DeleteExerciseInWorkoutUseCase {
    public JPAWorkoutExerciseAdapterResult<Workout> deleteExerciseInWorkout(Long workoutId, Long exerciseId);
}
