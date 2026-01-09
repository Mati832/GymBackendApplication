package application.port.out.ExercisePorts;

import application.commands.exercise.ExerciseFilter;
import domain.model.Exercise;

import java.util.List;

@FunctionalInterface
public interface LoadExercisesPort {
    public List<Exercise> loadExercises(ExerciseFilter filter, int page, int size);
}