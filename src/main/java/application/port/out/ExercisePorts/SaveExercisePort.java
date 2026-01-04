package application.port.out.ExercisePorts;

import domain.model.Exercise;

@FunctionalInterface
public interface SaveExercisePort {
    public Exercise saveExercise(Exercise exercise);
}
