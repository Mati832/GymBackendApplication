package application.commands.member;

import application.commands.PaginationCommand;

public record GetAssignedWorkoutsCommand(Long requestedBy, Long memberId, Long coachId, PaginationCommand pagination) {
}
