package application.service;

import application.port.in.UserRegistrationUseCase;
import application.port.out.UserPort;
import domain.model.User;
import domain.valueobject.Gender;
import jakarta.inject.Inject;

import java.time.LocalDateTime;

public class UserService implements UserRegistrationUseCase {

    @Inject
    UserPort userPort;

    @Override//evtl. noch komplexere validierungen machen
    public User registerUser(String firstName, String lastName, String email, String password, Gender gender, LocalDateTime bornOn) {
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            throw new IllegalArgumentException("First Name, Last Name, email and password cannot be empty");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("Password length should be at least 6 characters");
        }
        if (bornOn != null && bornOn.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("birth time must be after now");
        }
        if (userPort.findByEmail(email) != null) {
            throw new IllegalArgumentException("Email already exists");
        }

        return userPort.save(new User(firstName, lastName, email, password, gender, bornOn));
    }
}
