package application.port.out.UserPorts;

import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.Exercise;

@FunctionalInterface
public interface EditExercisePort {
    public JPAWorkoutExerciseAdapterResult<Exercise> editExercise(Long exerciseId, Exercise exercise);
}
