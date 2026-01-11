package application.service.member;

import application.commands.AuthenticatedUser;
import application.commands.PaginationCommand;
import application.commands.member.GetAssignedWorkoutsCommand;
import application.commands.member.GetAssignedWorkoutsFilterCommand;
import application.port.in.member.MemberGetsAssignedWorkoutsUsecase;
import application.port.out.AssignedWorkoutPorts.GetAssignedWorkoutsPort;
import application.port.out.UserPorts.FindUserByIdPort;
import application.service.AuthorizationService;
import domain.Results.member.AssignedWorkoutsResult;
import domain.dbResults.PagedResult;
import domain.model.AssignedWorkout;
import domain.model.Member;
import domain.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetAssignedWorkoutsService implements MemberGetsAssignedWorkoutsUsecase {

    @Inject
    GetAssignedWorkoutsPort getAssignedWorkoutsPort;
    @Inject
    FindUserByIdPort findUserByIdUseCase;
    @Inject
    AuthorizationService authorizationService;

    @Override
    public AssignedWorkoutsResult getWorkouts(GetAssignedWorkoutsCommand command) {

        AuthenticatedUser authenticatedUser=command.authenticatedUser();

        if (authenticatedUser == null) {
            return new AssignedWorkoutsResult.Failure(AssignedWorkoutsResult.Reason.UNAUTHORIZED);
        }
        if (!authenticatedUser.userId().equals(command.memberId())) {
            return new AssignedWorkoutsResult.Failure(AssignedWorkoutsResult.Reason.FORBIDDEN);
        }
        User requester = findUserByIdUseCase.findUserById(command.memberId());
        if (requester == null) {
            return new AssignedWorkoutsResult.Failure(AssignedWorkoutsResult.Reason.MEMBER_NOT_FOUND);
        }
        if (!authorizationService.isAuthorized(requester, Member.class)) {
            return new AssignedWorkoutsResult.Failure(AssignedWorkoutsResult.Reason.FORBIDDEN);
        }

        PaginationCommand pagination = command.pagination();
        GetAssignedWorkoutsFilterCommand filter = command.filter();
        int pageSize = pagination.size();
        int offset = pagination.offset();
        if (pagination.size() > 100) {
            pageSize = 100;
        }
        if (offset < 0) {
            offset = 0;
        }


        PagedResult<AssignedWorkout> assignedWorkouts = getAssignedWorkoutsPort.getAssignedWorkouts(command.memberId(), filter.coachId(), filter.search(), offset, pageSize);
        return new AssignedWorkoutsResult.Success(assignedWorkouts);
    }
}
