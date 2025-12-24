package application.port.out.UserPorts;

import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.User;

@FunctionalInterface
public interface DeleteWorkoutInUserPort {
    public JPAWorkoutExerciseAdapterResult<User> deleteWorkoutInUser(Long userId, Long workoutId);
}
