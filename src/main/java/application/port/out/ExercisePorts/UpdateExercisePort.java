package application.port.out.ExercisePorts;

import domain.model.Exercise;

@FunctionalInterface
public interface UpdateExercisePort {
    public Exercise updateExercise(Exercise exercise);
}
