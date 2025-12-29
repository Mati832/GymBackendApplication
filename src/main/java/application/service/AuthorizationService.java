package application.service;

import application.port.out.UserPorts.FindUserByIdPort;
import domain.model.User;
import jakarta.inject.Inject;

import java.util.List;

public class AuthorizationService {
    @Inject
    FindUserByIdPort findUserByIdPort;

    public <T extends User> boolean isAuthorized(Long userId, List<Class<T>> roles) {
        User userById = findUserByIdPort.findUserById(userId);
        return roles.contains(userById.getClass());
    }
}
