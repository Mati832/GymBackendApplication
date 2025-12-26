package domain.exceptions;

public class ExerciseSetNotFoundException extends IllegalArgumentException {
    public ExerciseSetNotFoundException(String message) {
        super(message);
    }
}
