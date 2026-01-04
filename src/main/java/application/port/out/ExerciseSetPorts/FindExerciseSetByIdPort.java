package application.port.out.ExerciseSetPorts;

import domain.model.ExerciseSet;

@FunctionalInterface
public interface FindExerciseSetByIdPort {
    public ExerciseSet findExerciseSetById(Long exerciseSetId);
}
