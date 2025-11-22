package application.service.member;

import application.port.in.FindUserByIdUseCase;
import application.port.in.member.MemberRegistrationUseCase;
import application.port.out.FindUserByEmailPort;
import application.port.out.FindUserByIdPort;
import application.port.out.SaveUserPort;
import static domain.Results.RegisterUserResult.FailureReason.*;

import application.service.UserService;
import domain.Results.RegisterUserResult;
import domain.model.Member;
import domain.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;

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
