package application.port.out.ExercisePorts;

import domain.model.Exercise;

@FunctionalInterface
public interface LoadExerciseByIdPort {
    public Exercise loadExerciseById(Long exerciseId);
}
