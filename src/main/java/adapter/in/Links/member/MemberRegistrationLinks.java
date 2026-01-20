package adapter.in.Links.member;

import adapter.in.controller.AuthenticationController;
import adapter.in.controller.CoachWebController;
import adapter.in.controller.MemberWebController;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

import static adapter.in.Links.LinkFactory.*;

public class MemberRegistrationLinks {
    public static URI selfUri( UriInfo uriInfo, Long memberId) {
        return uriInfo
                .getBaseUriBuilder()
                .path(MemberWebController.class)
                .path("/" + memberId)
                .build();
        //eigtl.build(memberId);
    }

    public static Link[] getLinks( UriInfo uriInfo, Long memberId) {
        return getUnauthenticatedLinks(uriInfo, memberId);

    }

    private static Link[] getUnauthenticatedLinks(UriInfo uriInfo, Long memberId) {
        return new Link[]{
                self(selfUri(uriInfo,memberId)),
                dispatcherLink(uriInfo),
                loginLink(uriInfo)
        };
    }

    public static Link[] getInvalidBirthdayLinks(UriInfo uriInfo) {
        return new Link[]{};
    }

    public static Link[] getUserAlreadyExistsLinks(UriInfo uriInfo) {
        return new Link[]{
            dispatcherLink(uriInfo),
            loginLink(uriInfo)
        };
    }

    public static Link[] getFieldEmptyLinks(UriInfo uriInfo) {
        return new Link[]{};
    }

    public static Link[] getPasswordTooWeakLinks(UriInfo uriInfo) {
        return new Link[]{};
    }

    public static Link[] getUnexpectedLinks(UriInfo uriInfo) {
        return new Link[]{};
    }
}
