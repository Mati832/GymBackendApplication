package adapter.in.Links;

import adapter.in.controller.DispatcherController;
import domain.valueobject.UserRole;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

import static adapter.in.Links.LinkFactory.*;

public class DispatcherLinks {

    private static URI selfUri(UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder().path(DispatcherController.class).build();
    }

    public static Link[] getUnauthenticatedLinks(UriInfo uriInfo) {
        return new Link[]{
                self(selfUri(uriInfo)),
                loginLink(uriInfo),
                coachRegisterLink(uriInfo),
                memberRegisterLink(uriInfo),
                getWorkoutsLink(uriInfo),
                getExercisesLink(uriInfo)
        };
    }

    public static Link[] getCoachLinks(UriInfo uriInfo, Long coachId) {
        return new Link[]{
                self(selfUri(uriInfo)),
                getUserLink(uriInfo,coachId, UserRole.COACH),
                getWorkoutsLink(uriInfo),
                getExercisesLink(uriInfo),
                getAssignedMembersLink(uriInfo,coachId)
        };
    }

    public static Link[] getMemberLinks(UriInfo uriInfo, Long memberId) {
        return new Link[]{
                self(selfUri(uriInfo)),
                getUserLink(uriInfo,memberId, UserRole.MEMBER),
                getWorkoutsLink(uriInfo),
                getExercisesLink(uriInfo),
                getAssignedCoachesLink(uriInfo,memberId),
                memberGetsAssignedWorkoutsLink(uriInfo,memberId)
        };
    }
}
