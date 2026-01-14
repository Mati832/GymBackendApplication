package adapter.in.DTOs.RequestDTOs.exercise;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record ExerciseRequest(Long id, @NotBlank String name, String type, Long durationInSec, LocalDateTime createdAt) {
}
