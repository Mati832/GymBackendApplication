package application.port.in.exerciseSet;

import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.ExerciseSet;

@FunctionalInterface
public interface LoadExerciseSetByIdUseCase {
    public JPAWorkoutExerciseAdapterResult<ExerciseSet> loadExerciseSetById(Long exerciseSetId);
}
