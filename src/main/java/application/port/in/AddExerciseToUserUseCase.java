package application.port.in;

import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.Exercise;
import domain.model.User;

@FunctionalInterface
public interface AddExerciseToUserUseCase {
    public JPAWorkoutExerciseAdapterResult<User> addExerciseToUser(Long userId, Exercise exercise);
}
