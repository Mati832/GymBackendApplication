package application.port.out.UserPorts;

import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.Exercise;
import domain.model.User;

@FunctionalInterface
public interface AddExerciseToUserPort {
    public JPAWorkoutExerciseAdapterResult<User> addExerciseToUser(Long userId, Exercise exercise);
}
