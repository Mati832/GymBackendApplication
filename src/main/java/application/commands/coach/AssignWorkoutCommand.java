package application.commands.coach;

import application.commands.AuthenticatedUser;

public record AssignWorkoutCommand(AuthenticatedUser authenticatedUser, Long coachId, Long memberId, Long workoutId) {
}
