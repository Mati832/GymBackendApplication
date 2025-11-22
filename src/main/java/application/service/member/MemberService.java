package application.service.member;

import application.port.in.FindUserByIdUseCase;
import application.port.in.member.MemberRegistrationUseCase;
import domain.Results.RegisterUserResult;
import domain.model.Member;
import domain.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MemberService implements MemberRegistrationUseCase, FindUserByIdUseCase {

    @Inject
    MemberUserService userService;

    @Override
    public RegisterUserResult registerMember(Member member) {
        return userService.registerMember(member);
    }

    @Override
    public User findUserById(Long id) {
        return userService.findUserById(id);
    }
}
