package application.port.out.WorkoutPorts;

import domain.model.Workout;

@FunctionalInterface
public interface UpdateWorkoutPort {
    public Workout update(Workout workout);
}
