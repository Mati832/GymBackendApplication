package adapter.in.Links.coach;


import adapter.in.controller.CoachWebController;
import domain.valueobject.UserRole;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

import static adapter.in.Links.LinkFactory.*;

public class CoachAssignsWorkoutLinks {

    public static Link[] getLinks(UriInfo uriInfo, Long coachId, Long workoutId, Long memberId) {
        return getCoachLinks(uriInfo, coachId, workoutId, memberId);

    }

    private static Link[] getCoachLinks(UriInfo uriInfo, Long coachId, Long workoutId, Long memberId) {
        return new Link[]{
                self(selfUri(uriInfo, workoutId, coachId)),
                getWorkoutlLink(uriInfo, workoutId),
                getUserLink(uriInfo, coachId, UserRole.COACH),
                getUserLink(uriInfo, memberId, UserRole.MEMBER),
                dispatcherLink(uriInfo)
        };
    }

    public static URI selfUri(UriInfo uriInfo, Long workoutId, Long coachId) {
        return uriInfo.getBaseUriBuilder().path(CoachWebController.class).path("/" + coachId + "/workouts/" + workoutId).build();
    }


    public static Link[] getForbiddenLinks(UriInfo uriInfo) {
        return null;
    }

    public static Link[] getCoachNotFoundLinks(UriInfo uriInfo) {
        return null;
    }

    public static Link[] getMemberNotFoundLinks(UriInfo uriInfo) {
        return null;
    }

    public static Link[] getEmptyFieldLinks(UriInfo uriInfo) {
        return null;
    }

    public static Link[] getUnauthorziedLinks(UriInfo uriInfo) {
        return null;
    }

    public static Link[] getWorkoutNotFoundLinks(UriInfo uriInfo) {
        return null;
    }

    public static Link[] getNotWithMemberAssignedLinks(UriInfo uriInfo) {
        return null;
    }
}
