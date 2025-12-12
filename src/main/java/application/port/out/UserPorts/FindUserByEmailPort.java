package application.port.out.UserPorts;

import domain.model.User;

@FunctionalInterface
public interface FindUserByEmailPort {
    User findByEmail(String email);
}
