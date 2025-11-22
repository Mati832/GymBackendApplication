package application.port.in;

import application.port.in.DTOs.UserLoginRequest;
import domain.Results.LoginUserResult;

public interface UserLoginUseCase {
    LoginUserResult loginUser(UserLoginRequest userLoginRequest);
}
