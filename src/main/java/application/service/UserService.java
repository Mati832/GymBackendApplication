package application.service;

import application.port.in.UserRegistrationUseCase;
import application.port.out.FindUserByEmailPort;
import application.port.out.SaveUserPort;
import domain.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;

@ApplicationScoped
public class UserService implements UserRegistrationUseCase {

    @Inject
    FindUserByEmailPort findUserByEmailPort;

    @Inject
    SaveUserPort saveUserPort;

    @Override//evtl. noch komplexere validierungen machen
    public User registerUser(User user) {
        if (user.getFirstName().isEmpty() || user.getLastName().isEmpty() || user.getEmail().isEmpty() || user.getPassword().isEmpty() || user.getBornOn() == null || user.getGender() == null) {
            throw new IllegalArgumentException("Fields cannot be empty");
        }
        if (user.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password length should be at least 6 characters");
        }
        if (user.getBornOn().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Birth date cannot be in the future");
        }
        if (findUserByEmailPort.findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("Email already exists");
        }

        return saveUserPort.save(user);
    }
}
