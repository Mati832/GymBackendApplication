package application.port.out.ExerciseSetPorts;

import application.commands.exerciseSet.ExerciseSetFilter;

@FunctionalInterface
public interface CountExerciseSetsPort {
    public int countExerciseSets(ExerciseSetFilter filter);
}
