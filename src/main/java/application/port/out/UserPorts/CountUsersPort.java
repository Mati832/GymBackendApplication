package application.port.out.UserPorts;

import application.commands.UserFilter;

@FunctionalInterface
public interface CountUsersPort {
    public int countUsers(UserFilter filter, int page, int size);
}
