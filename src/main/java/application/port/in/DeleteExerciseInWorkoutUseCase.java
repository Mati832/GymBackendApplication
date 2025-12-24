package application.port.in;

import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.Exercise;
import domain.model.Workout;

@FunctionalInterface
public interface DeleteExerciseInWorkoutUseCase {
    public JPAWorkoutExerciseAdapterResult<Workout> deleteExerciseInWorkout(Long workoutId, Long exerciseId);
}
