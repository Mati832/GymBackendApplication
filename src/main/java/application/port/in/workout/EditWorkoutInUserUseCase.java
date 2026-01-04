package application.port.in.workout;

import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.Workout;

@FunctionalInterface
public interface EditWorkoutInUserUseCase {
    public JPAWorkoutExerciseAdapterResult<Workout> editWorkoutInUser(Long workoutId, Workout workout);
}
