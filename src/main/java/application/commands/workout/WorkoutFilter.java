package application.commands.workout;

import java.time.LocalDateTime;

public record WorkoutFilter(Long userId, String name, LocalDateTime createdBefore, LocalDateTime createdAfter) {
}
