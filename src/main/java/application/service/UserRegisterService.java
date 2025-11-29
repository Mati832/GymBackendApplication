package application.service;

import application.commands.member.MemberRegisterCommand;
import application.port.out.FindUserByEmailPort;
import application.port.out.SaveUserPort;
import domain.Results.RegisterUserResult;
import domain.model.Member;
import domain.model.User;
import jakarta.inject.Inject;

import java.time.LocalDate;

import static domain.Results.RegisterUserResult.UserRegisterFailureReason.*;


public abstract class UserRegisterService<U extends User> {

    @Inject
    FindUserByEmailPort findUserByEmailPort;

    @Inject
    SaveUserPort saveUserPort;


    //evtl. noch komplexere validierungen machen
    public RegisterUserResult registerMember(MemberRegisterCommand member) {
        if (member.firstname().isEmpty() || member.lastName().isEmpty() || member.email().isEmpty() || member.password().isEmpty() || member.bornOn() == null || member.gender() == null) {
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
