package application.port.out;

import domain.model.User;

@FunctionalInterface
public interface FindUserByIdPort {
    User findUserById(Long id);
}
