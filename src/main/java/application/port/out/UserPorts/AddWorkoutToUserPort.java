package application.port.out.UserPorts;

import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.User;
import domain.model.Workout;

@FunctionalInterface
public interface AddWorkoutToUserPort {
    public JPAWorkoutExerciseAdapterResult<User> addWorkoutToUser(Long userId, Workout workout);
}
