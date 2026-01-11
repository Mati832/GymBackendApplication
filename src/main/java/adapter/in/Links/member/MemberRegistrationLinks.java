package adapter.in.Links.member;

import adapter.in.controller.AuthenticationController;
import adapter.in.controller.MemberWebController;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

public class MemberRegistrationLinks {
    public static URI getSelfUri(Long memberId, UriInfo uriInfo) {

        return uriInfo
                .getBaseUriBuilder()
                .path("todo")
                .path(MemberWebController.class)
                .path("/" + memberId)
                .build();
        //eigtl .build(memberId);
    }

    public static Link[] getLinks( Long memberId ,UriInfo uriInfo) {

        Link self = Link.fromUri(getSelfUri(memberId, uriInfo)).rel("self").build();

        Link login = Link.fromUri(uriInfo
                        .getBaseUriBuilder()
                        .path(AuthenticationController.class)
                        .path(AuthenticationController.class, "login")
                        .build())
                .rel("login")
                .build();


        return new Link[]{login, self};
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

    public static Link[] getUnexpectedLinks() {
        return new Link[]{};
    }
}
