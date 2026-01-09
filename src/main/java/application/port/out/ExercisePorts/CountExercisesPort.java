package application.port.out.ExercisePorts;

import application.commands.exercise.ExerciseFilter;

@FunctionalInterface
public interface CountExercisesPort {
    public int countExercises(ExerciseFilter filter);
}
