package adapter.in.Links.member;

import application.commands.AuthenticatedUser;
import domain.dbResults.PagedResult;
import domain.model.AssignedWorkout;
import domain.valueobject.UserRole;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static adapter.in.Links.LinkFactory.*;

public class MemberGetsAssignedWorkoutsLinks {


    public static Link[] getLinks(PagedResult<AssignedWorkout> result, UriInfo uriInfo) {
        return getMemberLinks(result, uriInfo);
    }

    private static Link[] getMemberLinks(PagedResult<AssignedWorkout> result, UriInfo uriInfo) {
        List<Link> links = new ArrayList<>();
        links.add(dispatcherLink(uriInfo));
        links.add(self(selfUri(uriInfo)));
        getPaginationLinks(result, uriInfo, links);
        return links.toArray(new Link[0]);
    }

    private static void getPaginationLinks(PagedResult<AssignedWorkout> result, UriInfo uriInfo, List<Link> links) {
        //next
        int nextOffset = result.offset() + result.size();
        if (nextOffset < result.totalCount()) {
            URI nextUri = uriInfo.getRequestUriBuilder()
                    .replaceQueryParam("offset", nextOffset)
                    .replaceQueryParam("size", result.size())
                    .build();

            links.add(Link.fromUri(nextUri).rel("next").build());
        }
        //prev
        if (result.offset() > 0) {
            int prevOffset = Math.max(0, result.offset() - result.size());
            URI prevUri = uriInfo.getRequestUriBuilder()
                    .replaceQueryParam("offset", prevOffset)
                    .replaceQueryParam("size", result.size())
                    .build();

            links.add(Link.fromUri(prevUri).rel("prev").build());
        }
    }

    private static URI selfUri(UriInfo uriInfo) {
        return uriInfo.getRequestUriBuilder().build();
    }

    public static Link[] getForbiddenLinks(UriInfo uriInfo, AuthenticatedUser user) {
        return new Link[]{
                dispatcherLink(uriInfo),
                getUserLink(uriInfo, user.userId(), user.role()),
                memberGetsAssignedWorkoutsLink(uriInfo, user.userId())
        };
    }

    public static Link[] getUnauthorizedLinks(UriInfo uriInfo) {
        return new Link[]{
                dispatcherLink(uriInfo),
                loginLink(uriInfo),
                coachRegisterLink(uriInfo),
                memberRegisterLink(uriInfo),
        };
    }

    public static Link[] getMemberNotFoundLinks(UriInfo uriInfo, AuthenticatedUser user) {

        return new Link[]{
                dispatcherLink(uriInfo),
                memberGetsAssignedWorkoutsLink(uriInfo, user.userId()),
                getWorkoutsLink(uriInfo)
        };
    }
}
