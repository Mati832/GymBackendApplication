package adapter.in.DTOs.RequestDTOs.exerciseSet;

import java.time.LocalDateTime;

public record ExerciseSetRequest(Long id, Integer reps, Double weightInKg, String notes,
                                 Long durationInSec, LocalDateTime createdAt) {
}
