package adapter.in.DTOs.RequestDTOs.exercise;

import java.time.LocalDateTime;

public record ExerciseRequest(Long id, String name, String type, Long durationInSec, LocalDateTime createdAt) {
}
