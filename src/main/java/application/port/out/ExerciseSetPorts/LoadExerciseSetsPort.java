package application.port.out.ExerciseSetPorts;

import application.commands.exerciseSet.ExerciseSetFilter;
import domain.model.ExerciseSet;

import java.util.List;

@FunctionalInterface
public interface LoadExerciseSetsPort {
    public List<ExerciseSet> loadExerciseSets(ExerciseSetFilter filter, int page, int size);
}
