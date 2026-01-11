package adapter.in.Links.coach;

import adapter.in.controller.CoachWebController;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

import static adapter.in.Links.LinkFactory.*;

public class CoachRegistrationLinks {
    public static URI selfUri( UriInfo uriInfo, Long coachId) {
        return uriInfo
                .getBaseUriBuilder()
                .path(CoachWebController.class)
                .path("/" + coachId)
                .build();
        //eigtl.build(coachId);
    }

    public static Link[] getLinks( UriInfo uriInfo, Long coachId) {
        return getUnauthenticatedLinks(uriInfo, coachId);

    }

    private static Link[] getUnauthenticatedLinks(UriInfo uriInfo, Long coachId) {
        return new Link[]{
                self(selfUri(uriInfo,coachId)),
                dispatcherLink(uriInfo),
                loginLink(uriInfo)
        };
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
