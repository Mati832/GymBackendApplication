package port.in.user;

import domain.DTOs.UserLoginRequest;
import domain.Results.LoginUserResult;

@FunctionalInterface
public interface UserLoginUseCase {
    LoginUserResult loginUser(UserLoginRequest userLoginRequest);
}
