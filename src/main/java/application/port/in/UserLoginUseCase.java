package application.port.in;

import application.commands.UserLoginCommand;
import domain.Results.LoginUserResult;

public interface UserLoginUseCase {
    LoginUserResult loginUser(UserLoginCommand userLoginCommand);
}
