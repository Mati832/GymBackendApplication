package application.port.out.WorkoutPorts;

import application.commands.workout.WorkoutFilter;
import domain.model.Workout;

import java.util.List;

@FunctionalInterface
public interface LoadWorkouts {
    public List<Workout> loadWorkouts(WorkoutFilter filter, int page, int size);
}
