package application.port.out.WorkoutPorts;

import domain.model.Workout;

@FunctionalInterface
public interface FindWorkoutByIdPort {
    public Workout findWorkoutById(Long workoutId);
}
