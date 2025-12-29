package adapter.in.Links.coach;

import adapter.in.DTOs.ResponseDTOs.CoachMemberResponse;
import adapter.in.controller.MemberWebController;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

public class CoachAssignsMemberLinks {
    public static URI getSelfUri(CoachMemberResponse dto, UriInfo uriInfo) {
        return uriInfo
                .getBaseUriBuilder()
                .path(MemberWebController.class)
                .path(MemberWebController.class, "assignCoach")
                .build(dto.getId());
    }

    public static Link[] getLinks(CoachMemberResponse dto, UriInfo uriInfo) {
        URI self = uriInfo
                .getBaseUriBuilder()
                .path(MemberWebController.class)
                .path(MemberWebController.class, "assignCoach")
                .build(dto.getId());
        Link selfLink = Link.fromUri(self).rel("self").build();
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

    public static Link[] getUnauthorizedLinks(UriInfo uriInfo) {
        return new Link[0];
    }

    public static Link[] getForbiddenLinks(UriInfo uriInfo) {
        return new Link[0];
    }
}
