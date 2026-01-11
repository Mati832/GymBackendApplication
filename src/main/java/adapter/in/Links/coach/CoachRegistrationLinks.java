package adapter.in.Links.coach;

import adapter.in.controller.AuthenticationController;
import adapter.in.controller.CoachWebController;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

public class CoachRegistrationLinks {
    public static URI getSelfUri(Long coachId, UriInfo uriInfo) {
        return uriInfo
                .getBaseUriBuilder()
                .path("todo")
                .path(CoachWebController.class)
                .path("/" + coachId)
                .build();
        //eigtl.build(coachId);
    }

    public static Link[] getLinks(Long coachId, UriInfo uriInfo) {
        Link self = Link.fromUri(getSelfUri(coachId, uriInfo)).rel("self").build();

        Link login = Link.fromUri(
                uriInfo.getBaseUriBuilder()
                        .path(AuthenticationController.class)
                        .path(AuthenticationController.class, "login")
                        .build()
        ).rel("login").build();
        return new Link[]{self, login};
    }

    public static Link[] getInvalidBirthdayLinks() {
        return new Link[]{};
    }

    public static Link[] getUserAlreadyExistsLinks() {
        return new Link[]{};
    }

    public static Link[] getFieldEmptyLinks() {
        return new Link[]{};
    }

    public static Link[] getPasswordTooWeakLinks() {
        return new Link[]{};
    }

    public static Link[] getInvalidLicenseLinks() {
        return new Link[]{};
    }

    public static Link[] getUnexpectedLinks() {
        return new Link[]{};
    }
}
