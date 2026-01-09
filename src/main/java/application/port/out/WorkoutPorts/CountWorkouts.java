package application.port.out.WorkoutPorts;

import application.commands.workout.WorkoutFilter;

@FunctionalInterface
public interface CountWorkouts {
    public int countWorkouts(WorkoutFilter filter);
}
