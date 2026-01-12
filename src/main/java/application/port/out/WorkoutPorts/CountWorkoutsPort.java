package application.port.out.WorkoutPorts;

import application.commands.workout.WorkoutFilter;

@FunctionalInterface
public interface CountWorkoutsPort {
    public Long countWorkouts(WorkoutFilter filter);
}
