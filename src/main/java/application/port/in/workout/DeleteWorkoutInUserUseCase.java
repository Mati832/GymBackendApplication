package application.port.in.workout;

import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.User;

@FunctionalInterface
public interface DeleteWorkoutInUserUseCase {
    public JPAWorkoutExerciseAdapterResult<User> deleteWorkoutInUser(Long userId, Long workoutId);
}
