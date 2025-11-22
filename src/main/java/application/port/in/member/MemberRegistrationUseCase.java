package application.port.in.member;

import domain.Results.RegisterUserResult;
import domain.model.Member;
import domain.model.User;

@FunctionalInterface
public interface MemberRegistrationUseCase {
    RegisterUserResult registerMember(Member member);
}
