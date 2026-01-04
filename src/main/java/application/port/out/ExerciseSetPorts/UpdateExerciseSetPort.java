package application.port.out.ExerciseSetPorts;

import domain.model.ExerciseSet;

@FunctionalInterface
public interface UpdateExerciseSetPort {
    public ExerciseSet updateExerciseSet(ExerciseSet exerciseSet);
}
