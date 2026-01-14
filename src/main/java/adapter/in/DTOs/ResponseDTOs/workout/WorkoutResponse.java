package adapter.in.DTOs.ResponseDTOs.workout;

import java.time.LocalDateTime;

public record WorkoutResponse(String name, String description, LocalDateTime createdAt) {
}
