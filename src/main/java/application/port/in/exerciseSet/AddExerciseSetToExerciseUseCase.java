package application.port.in.exerciseSet;

import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.Exercise;
import domain.model.ExerciseSet;

@FunctionalInterface
public interface AddExerciseSetToExerciseUseCase {
    public JPAWorkoutExerciseAdapterResult<Exercise> addExerciseSetToExercise(Long exerciseId, ExerciseSet exerciseSet);
}
