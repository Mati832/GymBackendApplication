package application.port.in.exerciseSet;

import application.commands.exerciseSet.ExerciseSetFilter;
import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.ExerciseSet;

@FunctionalInterface
public interface LoadExerciseSetsUseCase {
    public JPAWorkoutExerciseAdapterResult<ExerciseSet> loadExerciseSets(ExerciseSetFilter filter, int page, int size);
}
