package adapter.in.mapper;

import adapter.in.DTOs.RequestDTOs.coach.AssignMemberDTO;
import adapter.in.DTOs.RequestDTOs.coach.RegisterCoachDTO;
import application.commands.AssignCoachMemberRelationCommand;
import application.commands.AuthenticatedUser;
import application.commands.coach.CoachRegisterCommand;
import domain.model.CoachMember;

public class CoachMapper {

    public static AssignCoachMemberRelationCommand toDomain(AssignMemberDTO dto, AuthenticatedUser requestedBy) {
        return new AssignCoachMemberRelationCommand(dto.coachEmail(), dto.memberEmail(), requestedBy);
    }

    public static CoachRegisterCommand toDomain(RegisterCoachDTO dto) {
        return new CoachRegisterCommand(dto.firstName(), dto.lastName(), dto.email(), dto.password(), dto.gender(), dto.bornOn());
    }
}
