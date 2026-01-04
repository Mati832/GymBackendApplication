package application.port.out.ExerciseSetPorts;

import domain.model.ExerciseSet;

@FunctionalInterface
public interface SaveExerciseSetPort {
    public ExerciseSet saveExerciseSet(ExerciseSet exerciseSet);
}
