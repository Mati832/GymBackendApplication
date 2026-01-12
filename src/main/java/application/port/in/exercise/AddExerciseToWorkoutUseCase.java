package application.port.in.exercise;

import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.Exercise;
import domain.model.Workout;

@FunctionalInterface
public interface AddExerciseToWorkoutUseCase {
    public JPAWorkoutExerciseAdapterResult<Exercise> addExerciseToWorkout(Long workoutId, Exercise exercise);
}
