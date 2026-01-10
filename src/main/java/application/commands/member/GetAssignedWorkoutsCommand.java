package application.commands.member;

import application.commands.PaginationCommand;

public record GetAssignedWorkoutsCommand(Long requestedBy, Long memberId, PaginationCommand pagination, GetAssignedWorkoutsFilterCommand filter) {
}
