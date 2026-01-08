package application.port.in.workout;

import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.Workout;

@FunctionalInterface
public interface LoadWorkoutByIdUseCase {
    public JPAWorkoutExerciseAdapterResult<Workout> loadWorkoutById(Long id);
}