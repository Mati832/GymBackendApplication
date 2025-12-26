package application.port.in;

import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.Exercise;

@FunctionalInterface
public interface EditExerciseInWorkoutUseCase {
    public JPAWorkoutExerciseAdapterResult<Exercise> editExerciseInWorkout(Long exerciseId, Exercise exercise);
}
