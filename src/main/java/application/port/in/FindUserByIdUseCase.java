package application.port.in;

import domain.model.User;

@FunctionalInterface
public interface FindUserByIdUseCase {
    User findUserById(Long id);
}
