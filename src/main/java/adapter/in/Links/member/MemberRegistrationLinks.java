package adapter.in.Links.member;

import adapter.in.controller.MemberWebController;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

public class MemberRegistrationLinks {
    public static URI getSelfUri(Long memberId, UriInfo uriInfo) {
        //todo das stimmt noch nicht hier sollte user ressource zurückgegeben werden
        return uriInfo
                .getBaseUriBuilder()
                .path(MemberWebController.class)
                .path(MemberWebController.class, "register")
                .build(memberId);
    }

    public static Link[] getLinks(Long memberId, UriInfo uriInfo) {
        Link self = Link.fromUri(getSelfUri(memberId, uriInfo)).build();
        return new Link[]{self};
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
