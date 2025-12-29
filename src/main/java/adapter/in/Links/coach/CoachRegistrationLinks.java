package adapter.in.Links.coach;

import adapter.in.controller.CoachWebController;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

public class CoachRegistrationLinks {
    public static URI getSelfUri(Long coachId, UriInfo uriInfo) {
        return uriInfo
                .getBaseUriBuilder()
                .path(CoachWebController.class)
                .path(CoachWebController.class, "register")
                .build(coachId);
    }
    public static Link[] getLinks(Long coachId, UriInfo uriInfo) {
        Link self= Link.fromUri(getSelfUri(coachId,uriInfo)).build();
        return new Link[]{self};
    }

    public static Link[]getInvalidBirthdayLinks(){
        return new Link[]{};
    }
    public static Link[]getUserAlreadyExistsLinks(){
        return new Link[]{};
    }
    public static Link[]getFieldEmptyLinks(){
        return new Link[]{};
    }
    public static Link[]getPasswordTooWeakLinks(){
        return new Link[]{};
    }
    public static Link[]getInvalidLicenseLinks(){
        return new Link[]{};
    }
    public static Link[]getUnexpectedLinks(){
        return new Link[]{};
    }
}
