package application.port.in.exercise;

import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.User;

@FunctionalInterface
public interface DeleteExerciseInUserUseCase {
    public JPAWorkoutExerciseAdapterResult<Void> deleteExerciseInUser(Long userId, Long exerciseId);
}
