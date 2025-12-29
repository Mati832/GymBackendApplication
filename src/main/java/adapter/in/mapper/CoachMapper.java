package adapter.in.mapper;

import adapter.in.DTOs.RequestDTOs.coach.AssignMemberDTO;
import adapter.in.DTOs.RequestDTOs.coach.RegisterCoachDTO;
import adapter.in.DTOs.ResponseDTOs.CoachMemberResponse;
import application.commands.AssignCoachMemberRelationCommand;
import application.commands.coach.CoachRegisterCommand;
import domain.model.CoachMember;

public class CoachMapper {

    public static AssignCoachMemberRelationCommand toDomain(AssignMemberDTO dto, Long requestedBy) {
        return new AssignCoachMemberRelationCommand(dto.coachEmail(), dto.memberEmail(), requestedBy);
    }

    public static CoachMemberResponse toDTO(CoachMember coachMember) {
        return new CoachMemberResponse(coachMember.getId(), coachMember.getCoachId(), coachMember.getMemberId(), coachMember.getAssignedAt());
    }

    public static CoachRegisterCommand toDomain(RegisterCoachDTO dto) {
        return new CoachRegisterCommand(dto.firstName(), dto.lastName(), dto.email(), dto.password(), dto.gender(), dto.bornOn());
    }
}
