package domain.Results;

import domain.model.CoachMember;

public sealed interface AssignCoachMemberRelationResult permits AssignCoachMemberRelationResult.Success, AssignCoachMemberRelationResult.Failure {

    record Success(CoachMember coachMember) implements AssignCoachMemberRelationResult {
    }

    record Failure(AssignRelationFailureReason reason) implements AssignCoachMemberRelationResult {
    }

    enum AssignRelationFailureReason {
        COACH_NOT_FOUND,
        MEMBER_NOT_FOUND,
        RELATION_ALREADY_EXISTS
    }
}
