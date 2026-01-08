package application.port.in.exercise;

import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.Exercise;

@FunctionalInterface
public interface LoadExerciseByIdUseCase {
    public JPAWorkoutExerciseAdapterResult<Exercise> loadExerciseById(Long exerciseId);
}
