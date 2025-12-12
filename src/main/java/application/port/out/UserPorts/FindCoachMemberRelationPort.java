package application.port.out.UserPorts;

import domain.model.CoachMember;

public interface FindCoachMemberRelationPort {
    CoachMember findRelationByCoachAndMember(Long coachId, Long memberId);
}
