package adapter.in.Links.member;

import domain.dbResults.PagedResult;
import domain.model.AssignedWorkout;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class MemberGetsAssignedWorkoutsLinks {


    public static Link[] getLinks(PagedResult<AssignedWorkout> result, UriInfo uriInfo) {
        List<Link> links = new ArrayList<>();
        //self
        links.add(Link.fromUriBuilder(uriInfo.getRequestUriBuilder()).rel("self").build());

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

        return links.toArray(new Link[0]);
    }

    public static Link[] getForbiddenLinks(UriInfo uriInfo) {
        return null;
    }

    public static Link[] getUnauthorizedLinks(UriInfo uriInfo) {
        return null;
    }

    public static Link[] getMemberNotFoundLinks(UriInfo uriInfo) {
        return null;
    }
}
