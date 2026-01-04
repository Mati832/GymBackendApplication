package application.port.in.workout;

import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.User;
import domain.model.Workout;

@FunctionalInterface
public interface AddWorkoutToUserUseCase {
    public JPAWorkoutExerciseAdapterResult<User> addWorkoutToUser(Long userId, Workout workout);
}
