package adapter.in.DTOs.RequestDTOs.workout;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record WorkoutRequest(Long id, @NotBlank String name, String description, LocalDateTime createdAt) {
}
