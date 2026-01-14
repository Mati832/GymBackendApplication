package adapter.in.DTOs.ResponseDTOs.exerciseSets;

import java.time.LocalDateTime;

public record ExerciseSetResponse(Integer reps, Double weightInKG, String notes, Long durationInSec, LocalDateTime createdAt) {
}
