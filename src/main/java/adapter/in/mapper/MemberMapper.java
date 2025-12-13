package adapter.in.mapper;

import adapter.in.DTOs.RequestDTOs.member.AssignCoachDTO;
import adapter.in.DTOs.ResponseDTOs.CoachMemberResponse;
import application.commands.AssignCoachMemberRelationCommand;
import domain.model.CoachMember;

public class MemberMapper {
    public static AssignCoachMemberRelationCommand toDomain(AssignCoachDTO dto) {
        return new AssignCoachMemberRelationCommand(dto.coachEmail(), dto.memberEmail());
    }

    public static CoachMemberResponse toDTO(CoachMember coachMember) {
        return new CoachMemberResponse(coachMember.getId(), coachMember.getCoachId(), coachMember.getMemberId(), coachMember.getAssignedAt());
    }
}
