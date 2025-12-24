package application.port.out.UserPorts;

import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.User;

@FunctionalInterface
public interface DeleteExerciseInUserPort {
    public JPAWorkoutExerciseAdapterResult<User> deleteExerciseInUser(Long userId, Long exerciseId);
}
