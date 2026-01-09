package adapter.in.DTOs.ResponseDTOs;

import java.net.URI;
import java.time.LocalDateTime;

public record AssignedWorkoutResponse(URI self, URI memberLink, URI coachLink, URI workoutLink,
                                      LocalDateTime assignedAt) {
}
