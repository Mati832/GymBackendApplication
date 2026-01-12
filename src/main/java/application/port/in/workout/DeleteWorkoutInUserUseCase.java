package application.port.in.workout;

import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.User;

@FunctionalInterface
public interface DeleteWorkoutInUserUseCase {
    public JPAWorkoutExerciseAdapterResult<Void> deleteWorkoutInUser(Long userId, Long workoutId);
}
