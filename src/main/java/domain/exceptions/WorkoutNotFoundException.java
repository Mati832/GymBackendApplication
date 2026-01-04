package domain.exceptions;

public class WorkoutNotFoundException extends IllegalArgumentException {
    public WorkoutNotFoundException(String message) {
        super(message);
    }
}
