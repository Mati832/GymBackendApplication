package application.port.out.UserPorts;

import domain.model.User;

@FunctionalInterface
public interface SaveUserPort {
    User save(User user);
}
