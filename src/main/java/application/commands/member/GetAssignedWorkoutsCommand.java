package application.commands.member;

public record GetAssignedWorkoutsCommand(Long requestedBy, Long memberId, Long coachId) {
}
