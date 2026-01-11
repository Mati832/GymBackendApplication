package adapter.in.Links.member;

import domain.valueobject.UserRole;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

import static adapter.in.Links.LinkFactory.*;

public class MemberAssignsCoachLinks {


    public static URI selfUri( UriInfo uriInfo, Long relationId) {
        return uriInfo.getBaseUriBuilder().path("/coach-members/"+ relationId ).build();
    }

    public static Link[] getLinks( UriInfo uriInfo, Long memberId, Long relationId, Long coachId) {

        return getMemberLinks( uriInfo, memberId, relationId, coachId);
    }

    private static Link[] getMemberLinks( UriInfo uriInfo,Long memberId,Long relationId, Long coachId) {
        return new Link[]{
                self(selfUri(uriInfo, relationId)),
                dispatcherLink(uriInfo),
                getAssignedCoachesLink(uriInfo, memberId),
                getUserLink(uriInfo,memberId, UserRole.MEMBER),
                getUserLink(uriInfo,coachId, UserRole.COACH),
        };
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
