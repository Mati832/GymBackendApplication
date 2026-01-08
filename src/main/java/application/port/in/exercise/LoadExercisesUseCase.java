package application.port.in.exercise;

import application.commands.exercise.ExerciseFilter;
import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.Exercise;

@FunctionalInterface
public interface LoadExercisesUseCase {
    public JPAWorkoutExerciseAdapterResult<Exercise> loadExercises(ExerciseFilter filter, int page, int size);
}
