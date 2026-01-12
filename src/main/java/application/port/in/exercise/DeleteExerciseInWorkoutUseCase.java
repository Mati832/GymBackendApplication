package application.port.in.exercise;

import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.Workout;

@FunctionalInterface
public interface DeleteExerciseInWorkoutUseCase {
    public JPAWorkoutExerciseAdapterResult<Void> deleteExerciseInWorkout(Long userId, Long workoutId, Long exerciseId);
}
