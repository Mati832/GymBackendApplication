package application.port.out;

import domain.model.User;

public interface FindUserByEmailPort {
    User findByEmail(String email);
}
