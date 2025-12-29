package application.service.member;

import application.commands.member.MemberRegisterCommand;
import application.port.in.member.MemberRegistrationUseCase;
import application.port.out.UserPorts.FindUserByEmailPort;
import application.port.out.UserPorts.SaveUserPort;
import domain.Results.RegisterUserResult;
import domain.model.Member;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;

import static domain.Results.RegisterUserResult.UserRegisterFailureReason.*;
import static domain.Results.RegisterUserResult.UserRegisterFailureReason.USER_ALREADY_EXISTS;

@ApplicationScoped
public class MemberRegisterService implements MemberRegistrationUseCase {
    @Inject
    FindUserByEmailPort findUserByEmailPort;

    @Inject
    SaveUserPort saveUserPort;

    @Override
    public RegisterUserResult registerMember(MemberRegisterCommand member) {
        if (member.firstname()==null || member.lastName()==null || member.email()==null || member.password()==null || member.bornOn() == null || member.gender() == null) {
            return new RegisterUserResult.Failure(FIELD_EMPTY);
        }
        if (member.password().length() < 6) {
            return new RegisterUserResult.Failure(PASSWORD_TOO_WEAK);
        }
        if (member.bornOn().isAfter(LocalDate.now())) {
            return new RegisterUserResult.Failure(INVALID_BIRTHDAY);
        }
        if (findUserByEmailPort.findByEmail(member.email()) != null) {
            return new RegisterUserResult.Failure(USER_ALREADY_EXISTS);
        }
        Member newMember= new Member(member.firstname(), member.lastName(), member.email(), member.password(),member.gender(),member.bornOn());
        return new RegisterUserResult.Success(saveUserPort.save(newMember));
    }
}
