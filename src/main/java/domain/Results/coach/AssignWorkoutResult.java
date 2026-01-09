package domain.Results.coach;

import domain.model.AssignedWorkout;

public sealed interface AssignWorkoutResult permits AssignWorkoutResult.Success, AssignWorkoutResult.Failure {
    record Success(AssignedWorkout assignedWorkout) implements AssignWorkoutResult {
    }

    record Failure(Reason reason) implements AssignWorkoutResult {
    }

    enum Reason {
        EMTPY_FIELD,
        FORBIDDEN,
        UNAUTHORZIED,
        COACH_NOT_FOUND,
        WORKOUT_NOT_FOUND,
        MEMBER_NOT_FOUND,
        NOT_WITH_MEMBER_ASSIGNED
    }
}
