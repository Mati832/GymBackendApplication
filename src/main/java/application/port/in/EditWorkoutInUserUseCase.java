package application.port.in;

import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.Workout;

@FunctionalInterface
public interface EditWorkoutInUserUseCase {
    public JPAWorkoutExerciseAdapterResult<Workout> editWorkoutInUser(Long userId, Workout workout);
}
