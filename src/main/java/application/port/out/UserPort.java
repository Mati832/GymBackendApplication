package application.port.out;

import domain.model.User;

public interface UserPort {
    User save(User user);
    User findByEmail(String email);
    //find, delete...
}
