package adapter.in.mapper;

import adapter.in.DTOs.RequestDTOs.member.AssignCoachDTO;
import adapter.in.DTOs.RequestDTOs.member.RegisterMemberDTO;
import application.commands.AssignCoachMemberRelationCommand;
import application.commands.AuthenticatedUser;
import application.commands.member.MemberRegisterCommand;
import domain.model.CoachMember;

public class MemberMapper {
    public static AssignCoachMemberRelationCommand toDomain(AssignCoachDTO dto, AuthenticatedUser requestedBy) {
        return new AssignCoachMemberRelationCommand(dto.coachEmail(), dto.memberEmail(), requestedBy);
    }

    public static MemberRegisterCommand toDomain(RegisterMemberDTO dto) {
        return new MemberRegisterCommand(dto.firstName(), dto.lastName(), dto.email(), dto.password(), dto.gender(), dto.bornOn());
    }

}
