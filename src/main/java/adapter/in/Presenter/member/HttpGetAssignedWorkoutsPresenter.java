package adapter.in.Presenter.member;

import adapter.in.DTOs.ErrorResponse;
import adapter.in.DTOs.ResponseDTOs.member.AssignedWorkoutResponse;
import adapter.in.DTOs.ResponseDTOs.member.PagedAssignedWorkoutResponse;
import adapter.in.Links.member.MemberGetsAssignedWorkoutsLinks;
import adapter.in.mapper.AssignedWorkoutMapper;
import domain.Results.member.AssignedWorkoutsResult;
import domain.dbResults.PagedResult;
import domain.model.AssignedWorkout;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.List;


@ApplicationScoped
public class HttpGetAssignedWorkoutsPresenter {

    public Response toHttp(AssignedWorkoutsResult result, UriInfo uriInfo) {
        if (result instanceof AssignedWorkoutsResult.Failure failure) return getFailureResponse(failure, uriInfo);

        if (result instanceof AssignedWorkoutsResult.Success success) {
            PagedResult<AssignedWorkout> pagedResult = success.assignedWorkouts();

            List<AssignedWorkoutResponse> data = pagedResult
                    .data().stream()
                    .map(wo -> AssignedWorkoutMapper.toResponse(wo, uriInfo)).toList();
            PagedAssignedWorkoutResponse body = new PagedAssignedWorkoutResponse(data, pagedResult.totalCount(), pagedResult.offset(), pagedResult.size());

            Link[] links = MemberGetsAssignedWorkoutsLinks.getLinks(pagedResult, uriInfo);
            return Response.status(Response.Status.OK).entity(body).links(links).build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    private Response getFailureResponse(AssignedWorkoutsResult.Failure failure, UriInfo uriInfo) {
        Link[] links;
        ErrorResponse errorResponse = switch (failure.reason()) {
            case FORBIDDEN -> {
                links = MemberGetsAssignedWorkoutsLinks.getForbiddenLinks(uriInfo);
                yield new ErrorResponse(Response.Status.FORBIDDEN, failure.reason().name(), "");
            }
            case UNAUTHORIZED -> {
                links = MemberGetsAssignedWorkoutsLinks.getUnauthorizedLinks(uriInfo);
                yield new ErrorResponse(Response.Status.UNAUTHORIZED, failure.reason().name(), "");
            }
            case COACH_NOT_FOUND -> {
                links = MemberGetsAssignedWorkoutsLinks.getCoachNotFoundLinks(uriInfo);
                yield new ErrorResponse(Response.Status.NOT_FOUND, failure.reason().name(), "");
            }
            case MEMBER_NOT_FOUND -> {
                links = MemberGetsAssignedWorkoutsLinks.getMemberNotFoundLinks(uriInfo);
                yield new ErrorResponse(Response.Status.NOT_FOUND, failure.reason().name(), "");
            }
        };
        return Response.status(errorResponse.status()).links(links).entity(errorResponse).build();
    }
}
