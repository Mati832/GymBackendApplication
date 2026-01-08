package application.port.out.UserPorts;

import application.commands.UserFilter;
import domain.model.User;

import java.util.List;

@FunctionalInterface
public interface LoadUsersPort {
    public List<User> loadUsers(UserFilter filter, int page, int size);
}
