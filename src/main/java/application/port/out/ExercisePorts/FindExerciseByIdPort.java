package application.port.out.ExercisePorts;

import domain.model.Exercise;

@FunctionalInterface
public interface FindExerciseByIdPort {
    public Exercise findExerciseById(Long exerciseId);
}
