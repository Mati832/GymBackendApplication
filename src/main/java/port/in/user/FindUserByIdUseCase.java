package port.in.user;

import domain.model.User;

@FunctionalInterface
public interface FindUserByIdUseCase {
    User findUserById(Long id);
}
