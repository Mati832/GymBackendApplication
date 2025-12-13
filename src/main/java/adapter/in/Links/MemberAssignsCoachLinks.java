package adapter.in.Links;

import adapter.in.Controller.MemberWebController;
import adapter.in.DTOs.ResponseDTOs.CoachMemberResponse;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

public class MemberAssignsCoachLinks {
    public static URI getSelfUri(CoachMemberResponse dto, UriInfo uriInfo) {
        return uriInfo
                .getBaseUriBuilder()
                .path(MemberWebController.class)
                .path(MemberWebController.class, "assign")
                .build(dto.getId());
    }

    public static Link[] getLinks(CoachMemberResponse dto, UriInfo uriInfo) {
        URI self = uriInfo
                .getBaseUriBuilder()
                .path(MemberWebController.class)
                .path(MemberWebController.class, "assign")
                .build(dto.getId());
        Link selfLink = Link.fromUri(self).rel("self").build();
        //dann im verlauf alle links hinzufügen
        return new Link[]{selfLink};
    }

    public static Link[] getCoachNotFoundLinks(UriInfo uriInfo) {
        //todo
        return new Link[0];
    }

    public static Link[] getMemberNotFoundLinks(UriInfo uriInfo) {
        return new Link[0];
    }

    public static Link[] getRelationAlreadyExistsLinks(UriInfo uriInfo) {
        return new Link[0];
    }

}
