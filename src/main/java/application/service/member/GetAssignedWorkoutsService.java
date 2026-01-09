package application.service.member;

import application.commands.member.GetAssignedWorkoutsCommand;
import application.port.in.member.MemberGetsAssignedWorkoutsUsecase;
import application.port.out.AssignedWorkoutPorts.GetAssignedWorkoutsPort;
import application.port.out.UserPorts.FindUserByIdPort;
import application.service.AuthorizationService;
import domain.Results.member.AssignedWorkoutsResult;
import domain.model.AssignedWorkout;
import domain.model.Coach;
import domain.model.Member;
import domain.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

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


        if (command.requestedBy() == null) {
            return new AssignedWorkoutsResult.Failure(AssignedWorkoutsResult.Reason.UNAUTHORIZED);
        }
        if (!command.requestedBy().equals(command.memberId())) {
            return new AssignedWorkoutsResult.Failure(AssignedWorkoutsResult.Reason.FORBIDDEN);
        }
        User requester = findUserByIdUseCase.findUserById(command.memberId());
        if (requester == null) {
            return new AssignedWorkoutsResult.Failure(AssignedWorkoutsResult.Reason.MEMBER_NOT_FOUND);
        }
        if (!authorizationService.isAuthorized(requester, Member.class)) {
            return new AssignedWorkoutsResult.Failure(AssignedWorkoutsResult.Reason.UNAUTHORIZED);
        }
        if (command.coachId() != null) {
            User coach = findUserByIdUseCase.findUserById(command.coachId());
            if (!(coach instanceof Coach))
                return new AssignedWorkoutsResult.Failure(AssignedWorkoutsResult.Reason.COACH_NOT_FOUND);
        }

        List<AssignedWorkout> assignedWorkouts = getAssignedWorkoutsPort.getAssignedWorkouts(command.memberId(), command.coachId());
        return new AssignedWorkoutsResult.Success(assignedWorkouts);
    }
}
