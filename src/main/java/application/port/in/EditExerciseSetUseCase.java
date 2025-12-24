package application.port.in;

import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.ExerciseSet;

@FunctionalInterface
public interface EditExerciseSetUseCase {
    public JPAWorkoutExerciseAdapterResult<ExerciseSet> editExerciseSet(Long exerciseSetId, ExerciseSet exerciseSet);
}
