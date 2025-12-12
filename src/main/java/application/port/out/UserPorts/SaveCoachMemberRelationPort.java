package application.port.out.UserPorts;

import domain.model.CoachMember;

public interface SaveCoachMemberRelationPort {
    CoachMember save(CoachMember coachMember);
}
