package application.port.in.exerciseSet;

import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.Exercise;
import domain.model.ExerciseSet;

@FunctionalInterface
public interface AddExerciseSetToExerciseUseCase {
    public JPAWorkoutExerciseAdapterResult<ExerciseSet> addExerciseSetToExercise(Long useId, Long exerciseId, ExerciseSet exerciseSet);
}
