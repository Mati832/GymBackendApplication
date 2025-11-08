package application.port.in;

import domain.model.User;

public interface UserRegistrationUseCase {
    //eventuell Parameter mit DTO ersetzen
    User registerUser(User user);
}
