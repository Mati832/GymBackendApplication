package adapter.in.Links.coach;

import domain.valueobject.UserRole;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

import static adapter.in.Links.LinkFactory.*;

public class CoachAssignsMemberLinks {
    public static URI selfUri( UriInfo uriInfo, Long relationId) {
        return uriInfo.getBaseUriBuilder().path("/coach-members/"+ relationId ).build();
    }

    public static Link[] getLinks( UriInfo uriInfo, Long coachId, Long relationId, Long memberId) {

        return getCoachLinks( uriInfo, coachId, relationId, memberId);
    }

    private static Link[] getCoachLinks( UriInfo uriInfo,Long coachId,Long relationId, Long memberId) {
        return new Link[]{
                self(selfUri(uriInfo, relationId)),
                dispatcherLink(uriInfo),
                getAssignedCoachesLink(uriInfo, coachId),
                getUserLink(uriInfo,coachId, UserRole.COACH),
                getUserLink(uriInfo,memberId, UserRole.MEMBER)
        };
    }

    public static Link[] getCoachNotFoundLinks(UriInfo uriInfo) {
        return new Link[]{
            dispatcherLink(uriInfo),
            coachRegisterLink(uriInfo)
        };
    }

    public static Link[] getMemberNotFoundLinks(UriInfo uriInfo) {
        return new Link[]{
            dispatcherLink(uriInfo),
            memberRegisterLink(uriInfo)
        };
    }

    public static Link[] getRelationAlreadyExistsLinks(UriInfo uriInfo) {
        return new Link[0];
    }

    public static Link[] getUnauthorizedLinks(UriInfo uriInfo) {
        return new Link[]{
            dispatcherLink(uriInfo),
            loginLink(uriInfo),
            coachRegisterLink(uriInfo),
            memberRegisterLink(uriInfo)
        };
    }

    public static Link[] getForbiddenLinks(UriInfo uriInfo) {
        return new Link[]{
            dispatcherLink(uriInfo)
        };
    }
}
