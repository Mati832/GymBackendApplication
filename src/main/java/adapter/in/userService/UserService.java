package adapter.in.userService;

import domain.DTOs.UserLoginRequest;
import domain.Results.LoginUserResult;
import port.in.user.UserLoginUseCase;
import port.in.user.UserRegistrationUseCase;
import port.out.FindUserByEmailPort;
import port.out.SaveUserPort;
import domain.Results.RegisterUserResult;
import domain.model.User;
import jakarta.inject.Inject;

import java.time.LocalDateTime;

import static domain.Results.RegisterUserResult.UserFailureReason.*;


public abstract class UserService<U extends User> implements UserLoginUseCase, UserRegistrationUseCase<U> {

    @Inject
    FindUserByEmailPort findUserByEmailPort;

    @Inject
    SaveUserPort saveUserPort;


    //evtl. noch komplexere validierungen machen
    @Override
    public RegisterUserResult registerUser(U user) {
        if (user.getFirstName().isEmpty() || user.getLastName().isEmpty() || user.getEmail().isEmpty()
                || user.getPassword().isEmpty() || user.getBornOn() == null || user.getGender() == null) {
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

    @Override
    public LoginUserResult loginUser(UserLoginRequest userLoginRequest) {
        User byEmail = findUserByEmailPort.findByEmail(userLoginRequest.email());
        if (byEmail == null) {
            return new LoginUserResult.Failure(LoginUserResult.UserFailureReason.USER_NOT_FOUND);
        }
        if (!byEmail.getPassword().equals(userLoginRequest.password())) {
            return new LoginUserResult.Failure(LoginUserResult.UserFailureReason.WRONG_PASSWORD);
        }
        return new LoginUserResult.Success(byEmail);
    }
}
