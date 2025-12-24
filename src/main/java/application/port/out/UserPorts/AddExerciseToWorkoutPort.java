package application.port.out.UserPorts;

import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.Exercise;
import domain.model.Workout;

@FunctionalInterface
public interface AddExerciseToWorkoutPort {
    public JPAWorkoutExerciseAdapterResult<Workout> addExerciseToWorkout(Long workoutId, Exercise exercise);
}
