package application.port.out.UserPorts;

import domain.model.User;

@FunctionalInterface
public interface FindUserByIdPort {
    User findUserById(Long id);
}
