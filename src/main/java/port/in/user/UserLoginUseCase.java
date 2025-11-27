package port.in.user;

import domain.DTOs.UserLoginRequest;
import domain.Results.LoginUserResult;

public interface UserLoginUseCase {
    LoginUserResult loginUser(UserLoginRequest userLoginRequest);
}
