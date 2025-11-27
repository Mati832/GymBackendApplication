package port.out;

import domain.model.User;

@FunctionalInterface
public interface SaveUserPort {
    User save(User user);
}
