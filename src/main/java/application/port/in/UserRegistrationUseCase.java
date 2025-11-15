package application.port.in;

import domain.Results.RegisterUserResult;
import domain.model.User;

public interface UserRegistrationUseCase {
    RegisterUserResult registerUser(User user);
}
