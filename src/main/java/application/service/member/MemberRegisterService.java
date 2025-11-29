package application.service.member;

import application.commands.member.MemberRegisterCommand;
import application.port.in.member.MemberRegistrationUseCase;
import domain.Results.RegisterUserResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MemberRegisterService implements MemberRegistrationUseCase {

    @Inject
    MemberUserRegisterService userService;

    @Override
    public RegisterUserResult registerMember(MemberRegisterCommand member) {
        return userService.registerMember(member);
    }
}
