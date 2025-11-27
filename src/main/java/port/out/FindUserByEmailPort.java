package port.out;

import domain.model.User;

@FunctionalInterface
public interface FindUserByEmailPort {
    User findByEmail(String email);
}
