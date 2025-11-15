package application.service;

import application.port.in.UserRegistrationUseCase;
import application.port.out.FindUserByEmailPort;
import application.port.out.SaveUserPort;
import static domain.Results.RegisterUserResult.FailureReason.*;
import domain.Results.RegisterUserResult;
import domain.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;

@ApplicationScoped
public class UserService implements UserRegistrationUseCase {

    @Inject
    FindUserByEmailPort findUserByEmailPort;

    @Inject
    SaveUserPort saveUserPort;

    @Override//evtl. noch komplexere validierungen machen
    public RegisterUserResult registerUser(User user) {
        if (user.getFirstName().isEmpty() || user.getLastName().isEmpty() || user.getEmail().isEmpty() || user.getPassword().isEmpty() || user.getBornOn() == null || user.getGender() == null) {
            return new RegisterUserResult.Failure(FIELD_EMPTY);
        }
        if (user.getPassword().length() < 6) {
            return new RegisterUserResult.Failure(PASSWORD_TOO_WEAK);
        }
        if (user.getBornOn().isAfter(LocalDateTime.now())) {
            return new RegisterUserResult.Failure(INVALID_BIRTHDAY);
        }
        if (findUserByEmailPort.findByEmail(user.getEmail()) != null) {
            return new RegisterUserResult.Failure(USER_ALREADY_EXISTS);
        }

        return new RegisterUserResult.Success(saveUserPort.save(user));
    }
}
