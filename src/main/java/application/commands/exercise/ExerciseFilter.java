package application.commands.exercise;

import java.time.LocalDateTime;

public record ExerciseFilter(Long userId, Long workoutId, String name, LocalDateTime createdBefore, LocalDateTime createdAfter) {
}
