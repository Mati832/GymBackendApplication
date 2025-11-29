package application.port.in.member;

import application.commands.MemberRegisterCommand;
import domain.Results.RegisterUserResult;

@FunctionalInterface
public interface MemberRegistrationUseCase {
    RegisterUserResult registerMember(MemberRegisterCommand member);
}
