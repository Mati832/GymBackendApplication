package application.port.out;

import domain.model.User;

public interface SaveUserPort {
    User save(User user);
}
