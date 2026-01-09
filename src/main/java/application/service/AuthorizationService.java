package application.service;

import application.port.out.UserPorts.FindUserByIdPort;
import domain.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class AuthorizationService {


    @SafeVarargs
    public final boolean isAuthorized(User user, Class<? extends User>... roles) {

        for (Class<? extends User> role : roles) {
            if (role.isInstance(user)) {
                return true;
            }
        }
        return false;
    }
}
