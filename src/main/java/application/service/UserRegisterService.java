package application.service;

import port.out.FindUserByEmailPort;
import port.out.SaveUserPort;
import domain.Results.RegisterUserResult;
import domain.model.Member;
import domain.model.User;
import jakarta.inject.Inject;

import java.time.LocalDateTime;

import static domain.Results.RegisterUserResult.UserFailureReason.*;


public abstract class UserRegisterService<U extends User> {

    @Inject
    FindUserByEmailPort findUserByEmailPort;

    @Inject
    SaveUserPort saveUserPort;


    //evtl. noch komplexere validierungen machen
    public RegisterUserResult registerMember(Member member) {
        if (member.getFirstName().isEmpty() || member.getLastName().isEmpty() || member.getEmail().isEmpty() || member.getPassword().isEmpty() || member.getBornOn() == null || member.getGender() == null) {
            return new RegisterUserResult.Failure(FIELD_EMPTY);
        }
        if (member.getPassword().length() < 6) {
            return new RegisterUserResult.Failure(PASSWORD_TOO_WEAK);
        }
        if (member.getBornOn().isAfter(LocalDateTime.now())) {
            return new RegisterUserResult.Failure(INVALID_BIRTHDAY);
        }
        if (findUserByEmailPort.findByEmail(member.getEmail()) != null) {
            return new RegisterUserResult.Failure(USER_ALREADY_EXISTS);
        }

        return new RegisterUserResult.Success(saveUserPort.save(member));
    }

}
