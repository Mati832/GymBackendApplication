package adapter.in.DTOs.RequestDTOs.workout;

import java.time.LocalDateTime;

public record WorkoutRequest(Long id, String name, String description, LocalDateTime createdAt) {
}
