package application.port.in;

import domain.model.User;
import domain.valueobject.Gender;

import java.time.LocalDateTime;

public interface UserRegistrationUseCase {
    //eventuell Parameter mit DTO ersetzen
    User registerUser(String firstName, String lastName, String email, String password, Gender gender, LocalDateTime bornOn);
}
