package application.port.out.WorkoutPorts;

import domain.model.Workout;

@FunctionalInterface
public interface LoadWorkoutByIdPort {
    public Workout laodWorkout(Long workoutId);
}
