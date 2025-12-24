package application.port.out.UserPorts;

import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.Workout;

@FunctionalInterface
public interface EditWorkoutInUserPort {
    public JPAWorkoutExerciseAdapterResult<Workout> editWorkoutInUser(Long workoutId, Workout workout);
}
