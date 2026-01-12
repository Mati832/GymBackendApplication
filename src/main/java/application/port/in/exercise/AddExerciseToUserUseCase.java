package application.port.in.exercise;

import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.Exercise;
import domain.model.User;

@FunctionalInterface
public interface AddExerciseToUserUseCase {
    public JPAWorkoutExerciseAdapterResult<Exercise> addExerciseToUser(Long userId, Exercise exercise);
}
