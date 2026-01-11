package application.commands.member;

import application.commands.AuthenticatedUser;
import application.commands.PaginationCommand;

public record GetAssignedWorkoutsCommand(AuthenticatedUser authenticatedUser, Long memberId, PaginationCommand pagination, GetAssignedWorkoutsFilterCommand filter) {
}
