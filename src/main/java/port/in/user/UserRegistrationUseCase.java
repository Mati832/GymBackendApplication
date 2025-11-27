package port.in.user;

import domain.Results.RegisterUserResult;
import domain.model.User;

@FunctionalInterface
public interface UserRegistrationUseCase<U extends User> {
    RegisterUserResult registerUser(U user);
}
