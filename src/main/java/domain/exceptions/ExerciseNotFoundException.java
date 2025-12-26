package domain.exceptions;

public class ExerciseNotFoundException extends IllegalArgumentException {
    public ExerciseNotFoundException(String message) {
        super(message);
    }
}
