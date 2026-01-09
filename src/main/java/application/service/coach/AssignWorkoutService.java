package application.service.coach;

import application.commands.coach.AssignWorkoutCommand;
import application.port.in.coach.AssignWorkoutUseCase;
import application.port.out.AssignedWorkoutPorts.CreateAssignedWorkoutPort;
import application.port.out.UserPorts.FindCoachMemberRelationPort;
import application.port.out.UserPorts.FindUserByIdPort;
import application.port.out.WorkoutPorts.FindWorkoutByIdPort;
import application.service.AuthorizationService;
import domain.Results.coach.AssignWorkoutResult;
import domain.model.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AssignWorkoutService implements AssignWorkoutUseCase {

    @Inject
    CreateAssignedWorkoutPort createAssignedWorkoutPort;
    @Inject
    FindUserByIdPort findUserByIdPort;
    @Inject
    FindWorkoutByIdPort findWorkoutByIdPort;
    @Inject
    AuthorizationService authorizationService;
    @Inject
    FindCoachMemberRelationPort findCoachMemberRelationPort;

    @Override
    public AssignWorkoutResult assign(AssignWorkoutCommand command) {
        if (command.workoutId() == null || command.coachId() == null || command.memberId() == null) {
            return new AssignWorkoutResult.Failure(AssignWorkoutResult.Reason.EMTPY_FIELD);
        }
        if (command.requestedBy() == null) {
            return new AssignWorkoutResult.Failure(AssignWorkoutResult.Reason.UNAUTHORZIED);
        }
        User requester = findUserByIdPort.findUserById(command.coachId());
        if (requester == null) {
            return new AssignWorkoutResult.Failure(AssignWorkoutResult.Reason.COACH_NOT_FOUND);
        }
        if (!command.requestedBy().equals(command.coachId()) || !authorizationService.isAuthorized(requester, Coach.class)) {
            return new AssignWorkoutResult.Failure(AssignWorkoutResult.Reason.FORBIDDEN);
        }
        User member = findUserByIdPort.findUserById(command.memberId());
        if (!(member instanceof Member)) {
            return new AssignWorkoutResult.Failure(AssignWorkoutResult.Reason.MEMBER_NOT_FOUND);
        }
        Workout workout = findWorkoutByIdPort.findWorkoutById(command.workoutId());
        if (workout == null) {
            return new AssignWorkoutResult.Failure(AssignWorkoutResult.Reason.WORKOUT_NOT_FOUND);
        }
        CoachMember relationByCoachAndMember = findCoachMemberRelationPort.findRelationByCoachAndMember(command.coachId(), command.memberId());
        if (relationByCoachAndMember == null) {
            return new AssignWorkoutResult.Failure(AssignWorkoutResult.Reason.NOT_WITH_MEMBER_ASSIGNED);
        }


        AssignedWorkout result = createAssignedWorkoutPort.createAssignedWorkout(new AssignedWorkout(command.workoutId(), command.memberId(), command.coachId()));
        return new AssignWorkoutResult.Success(result);
    }
}
