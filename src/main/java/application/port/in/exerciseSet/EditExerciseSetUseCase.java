package application.port.in.exerciseSet;

import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.ExerciseSet;

@FunctionalInterface
public interface EditExerciseSetUseCase {
    public JPAWorkoutExerciseAdapterResult<ExerciseSet> editExerciseSet(Long userId, Long exerciseSetId, ExerciseSet exerciseSet);
}
