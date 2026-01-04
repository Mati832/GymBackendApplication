package application.port.in.exercise;

import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.User;

@FunctionalInterface
public interface DeleteExerciseInUserUseCase {
    public JPAWorkoutExerciseAdapterResult<User> deleteExerciseInUser(Long userId, Long exerciseId);
}
