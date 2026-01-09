package application.port.in.member;

import application.commands.member.GetAssignedWorkoutsCommand;
import domain.Results.member.AssignedWorkoutsResult;

public interface MemberGetsAssignedWorkoutsUsecase {
    AssignedWorkoutsResult getWorkouts(GetAssignedWorkoutsCommand command);
}
