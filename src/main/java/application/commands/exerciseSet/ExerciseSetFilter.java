package application.commands.exerciseSet;

import java.time.LocalDateTime;

public record ExerciseSetFilter(Long exerciseId, Integer repsGreaterThan, Integer repsLessThan, Double weightGreaterThan, Double weightLessThan,
                                Long durationGreaterThan, Long durationLessThan, LocalDateTime createdBefore, LocalDateTime createdAfter) {
}
