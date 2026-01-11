package adapter.in.Presenter.coach;

import adapter.in.DTOs.ErrorResponse;
import adapter.in.Links.coach.CoachAssignsWorkoutLinks;
import domain.Results.coach.AssignWorkoutResult;
import domain.model.AssignedWorkout;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

@ApplicationScoped
public class HttpCoachAssignsWorkoutPresenter {

    public Response toHttp(AssignWorkoutResult result, UriInfo uriInfo) {
        if (result instanceof AssignWorkoutResult.Failure failure) {
            return getFailureResponse(failure, uriInfo);
        }
        if (result instanceof AssignWorkoutResult.Success success) {
            AssignedWorkout assignedWorkout = success.assignedWorkout();
            Link[] links = CoachAssignsWorkoutLinks.getLinks(uriInfo,assignedWorkout.getCoachId(), assignedWorkout.getWorkoutId(),  assignedWorkout.getMemberId());
            return Response.status(Response.Status.CREATED).links(links).build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    private Response getFailureResponse(AssignWorkoutResult.Failure failure, UriInfo uriInfo) {
        Link[] links;
        ErrorResponse errorResponse = switch (failure.reason()) {
            case FORBIDDEN -> {
                links = CoachAssignsWorkoutLinks.getForbiddenLinks(uriInfo);
                yield new ErrorResponse(Response.Status.FORBIDDEN, failure.reason().name(), "");
            }
            case COACH_NOT_FOUND -> {
                links = CoachAssignsWorkoutLinks.getCoachNotFoundLinks(uriInfo);
                yield new ErrorResponse(Response.Status.NOT_FOUND, failure.reason().name(), "");
            }
            case MEMBER_NOT_FOUND -> {
                links = CoachAssignsWorkoutLinks.getMemberNotFoundLinks(uriInfo);
                yield new ErrorResponse(Response.Status.NOT_FOUND, failure.reason().name(), "");
            }
            case EMTPY_FIELD -> {
                links = CoachAssignsWorkoutLinks.getEmptyFieldLinks(uriInfo);
                yield new ErrorResponse(Response.Status.BAD_REQUEST, failure.reason().name(), "");
            }
            case UNAUTHORZIED -> {
                links = CoachAssignsWorkoutLinks.getUnauthorziedLinks(uriInfo);
                yield new ErrorResponse(Response.Status.UNAUTHORIZED, failure.reason().name(), "");
            }
            case WORKOUT_NOT_FOUND -> {
                links = CoachAssignsWorkoutLinks.getWorkoutNotFoundLinks(uriInfo);
                yield new ErrorResponse(Response.Status.NOT_FOUND, failure.reason().name(), "");
            }
            case NOT_WITH_MEMBER_ASSIGNED -> {
                links = CoachAssignsWorkoutLinks.getNotWithMemberAssignedLinks(uriInfo);
                yield new ErrorResponse(Response.Status.FORBIDDEN, failure.reason().name(), "");
            }
        };
        return Response.status(errorResponse.status()).links(links).entity(errorResponse).build();
    }
}
