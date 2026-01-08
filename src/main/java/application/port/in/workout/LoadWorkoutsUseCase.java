package application.port.in.workout;

import application.commands.workout.WorkoutFilter;
import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.Workout;

@FunctionalInterface
public interface LoadWorkoutsUseCase {
    public JPAWorkoutExerciseAdapterResult<Workout> loadWorkouts(WorkoutFilter filter, int page, int size);
}
