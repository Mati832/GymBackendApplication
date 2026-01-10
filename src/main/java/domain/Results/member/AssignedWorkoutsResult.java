package domain.Results.member;

import domain.dbResults.PagedResult;
import domain.model.AssignedWorkout;

import java.util.List;

public sealed interface AssignedWorkoutsResult permits AssignedWorkoutsResult.Success, AssignedWorkoutsResult.Failure {
    record Success(PagedResult<AssignedWorkout> assignedWorkouts) implements AssignedWorkoutsResult {

    }

    record Failure(Reason reason) implements AssignedWorkoutsResult {
    }

    enum Reason {
        UNAUTHORIZED,
        FORBIDDEN,
        MEMBER_NOT_FOUND,
        COACH_NOT_FOUND
    }
}



