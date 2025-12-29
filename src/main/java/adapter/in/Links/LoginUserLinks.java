package adapter.in.Links;

import adapter.in.DTOs.ResponseDTOs.LoginUserResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.UriInfo;

@ApplicationScoped
public class LoginUserLinks {

    public static Link[] getLinks(LoginUserResponse dto, UriInfo uriInfo) {
        return new Link[0];
    }

    public static Link[] getUserNotFoundLinks(UriInfo uriInfo) {
        return new Link[0];
    }

    public static Link[] getWrongPasswordLinks(UriInfo uriInfo) {
        return new Link[0];
    }
}
