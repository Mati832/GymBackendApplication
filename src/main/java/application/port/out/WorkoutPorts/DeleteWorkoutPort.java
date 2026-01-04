package application.port.out.WorkoutPorts;

@FunctionalInterface
public interface DeleteWorkoutPort {
    public void deleteWorkout(Long workoutId);
}
