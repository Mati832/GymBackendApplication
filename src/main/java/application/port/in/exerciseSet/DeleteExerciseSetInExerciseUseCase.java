package application.port.in.exerciseSet;

import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.Exercise;

@FunctionalInterface
public interface DeleteExerciseSetInExerciseUseCase {
    public JPAWorkoutExerciseAdapterResult<Exercise> deleteExerciseSetInExercise(Long exerciseId, Long exerciseSetId);
}
