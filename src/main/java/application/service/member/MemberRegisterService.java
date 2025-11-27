package application.service.member;

import port.in.member.MemberRegistrationUseCase;
import domain.Results.RegisterUserResult;
import domain.model.Member;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MemberRegisterService implements MemberRegistrationUseCase {

    @Inject
    MemberUserRegisterService userService;

    @Override
    public RegisterUserResult registerMember(Member member) {
        return userService.registerMember(member);
    }

}
