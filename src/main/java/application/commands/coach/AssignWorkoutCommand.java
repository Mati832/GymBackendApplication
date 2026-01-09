package application.commands.coach;

public record AssignWorkoutCommand(Long requestedBy, Long coachId, Long memberId, Long workoutId) {
}
