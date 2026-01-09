package adapter.in.Links.coach;

import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

public class CoachAssignsWorkoutLinks {
    public static URI getSelf() {
        return null;
    }

    public static Link[] getLinks() {
        return null;
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
