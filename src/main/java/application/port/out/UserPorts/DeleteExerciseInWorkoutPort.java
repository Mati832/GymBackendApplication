package application.port.out.UserPorts;

import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.Exercise;
import domain.model.Workout;

@FunctionalInterface
public interface DeleteExerciseInWorkoutPort {
    public JPAWorkoutExerciseAdapterResult<Workout> deleteExerciseInWorkout(Long workoutId, Long exerciseId);
}
