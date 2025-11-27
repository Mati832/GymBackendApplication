package application.service;

import domain.DTOs.UserLoginRequest;
import port.in.user.UserLoginUseCase;
import port.out.FindUserByEmailPort;
import domain.Results.LoginUserResult;
import domain.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UserLoginService implements UserLoginUseCase {

    @Inject
    FindUserByEmailPort findUserByEmailPort;


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
