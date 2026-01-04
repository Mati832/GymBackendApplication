package application.port.out.WorkoutPorts;

import domain.model.Workout;

@FunctionalInterface
public interface SaveWorkoutPort {
    public Workout saveWorkout(Workout workout);
}
