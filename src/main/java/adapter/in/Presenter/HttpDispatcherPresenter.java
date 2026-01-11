package adapter.in.Presenter;

import adapter.in.Links.DispatcherLinks;
import application.commands.AuthenticatedUser;
import domain.valueobject.UserRole;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@ApplicationScoped
public class HttpDispatcherPresenter {

    public Response toHttp(UriInfo uriInfo, AuthenticatedUser user){
        Link[] links=switch (user){
            case null -> DispatcherLinks.getUnauthenticatedLinks(uriInfo);
            case AuthenticatedUser u when u.role() == UserRole.COACH->DispatcherLinks.getCoachLinks(uriInfo,u.userId());
            case AuthenticatedUser u when u.role() == UserRole.MEMBER->DispatcherLinks.getMemberLinks(uriInfo,u.userId());
            default -> throw new IllegalStateException("unexpected authenticatedUser: " + user);
        };

        return Response.ok().links(links).build();
    }
}
