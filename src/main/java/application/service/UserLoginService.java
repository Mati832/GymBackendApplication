package application.service;

import application.commands.UserLoginCommand;
import application.port.in.UserLoginUseCase;
import application.port.out.FindUserByEmailPort;
import domain.Results.LoginUserResult;
import domain.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UserLoginService implements UserLoginUseCase {

    @Inject
    FindUserByEmailPort findUserByEmailPort;


    @Override
    public LoginUserResult loginUser(UserLoginCommand userLoginCommand) {
        User byEmail = findUserByEmailPort.findByEmail(userLoginCommand.email());
        if (byEmail == null) {
            return new LoginUserResult.Failure(LoginUserResult.UserLoginFailureReason.USER_NOT_FOUND);
        }
        if (!byEmail.getPassword().equals(userLoginCommand.password())) {
            return new LoginUserResult.Failure(LoginUserResult.UserLoginFailureReason.WRONG_PASSWORD);
        }
        return new LoginUserResult.Success(byEmail);
    }
}
