package adapter.in.DTOs.ResponseDTOs.exercises;

import java.time.LocalDateTime;

public record ExerciseResponse(String name, String type, Long durationInSec, LocalDateTime createdAt) {
}
