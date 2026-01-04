package application.port.in.exercise;

import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.Exercise;

@FunctionalInterface
public interface EditExerciseUseCase {
    public JPAWorkoutExerciseAdapterResult<Exercise> editExercise(Long exerciseId, Exercise exercise);
}
