package adapter.in.Presenter.coach;

import adapter.in.DTOs.ErrorResponse;
import adapter.in.DTOs.ResponseDTOs.CoachMemberResponse;
import adapter.in.Links.coach.CoachAssignsMemberLinks;
import adapter.in.mapper.CoachMapper;
import domain.Results.AssignCoachMemberRelationResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;


@ApplicationScoped
public class HttpAssignMemberPresenter {

    public Response toHttp(AssignCoachMemberRelationResult result, UriInfo uriInfo) {
        if (result instanceof AssignCoachMemberRelationResult.Failure failure) {
            return getFailureResponse(failure, uriInfo);
        }
        if (result instanceof AssignCoachMemberRelationResult.Success success) {
            CoachMemberResponse dto = CoachMapper.toDTO(success.coachMember());
            URI selfUri = CoachAssignsMemberLinks.getSelfUri(dto, uriInfo);
            Link[] links = CoachAssignsMemberLinks.getLinks(dto, uriInfo);
            return Response.created(selfUri).links(links).build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    private Response getFailureResponse(AssignCoachMemberRelationResult.Failure failure, UriInfo uriInfo) {
        Link[] links;
        ErrorResponse response = switch (failure.reason()) {
            case AssignCoachMemberRelationResult.AssignRelationFailureReason.COACH_NOT_FOUND -> {
                links = CoachAssignsMemberLinks.getCoachNotFoundLinks(uriInfo);
                yield new ErrorResponse(Response.Status.NOT_FOUND, AssignCoachMemberRelationResult.AssignRelationFailureReason.COACH_NOT_FOUND.toString(), "");
            }
            case AssignCoachMemberRelationResult.AssignRelationFailureReason.MEMBER_NOT_FOUND -> {
                links = CoachAssignsMemberLinks.getMemberNotFoundLinks(uriInfo);
                yield new ErrorResponse(Response.Status.NOT_FOUND, AssignCoachMemberRelationResult.AssignRelationFailureReason.MEMBER_NOT_FOUND.toString(), "");
            }
            case AssignCoachMemberRelationResult.AssignRelationFailureReason.RELATION_ALREADY_EXISTS -> {
                links = CoachAssignsMemberLinks.getRelationAlreadyExistsLinks(uriInfo);
                yield new ErrorResponse(Response.Status.CONFLICT, AssignCoachMemberRelationResult.AssignRelationFailureReason.RELATION_ALREADY_EXISTS.toString(), "");
            }
            case AssignCoachMemberRelationResult.AssignRelationFailureReason.UNAUTHORIZED -> {
                links = CoachAssignsMemberLinks.getUnauthorizedLinks(uriInfo);
                yield new ErrorResponse(Response.Status.UNAUTHORIZED, AssignCoachMemberRelationResult.AssignRelationFailureReason.UNAUTHORIZED.toString(), "");
            }
            case AssignCoachMemberRelationResult.AssignRelationFailureReason.FORBIDDEN -> {
                links = CoachAssignsMemberLinks.getForbiddenLinks(uriInfo);
                yield new ErrorResponse(Response.Status.FORBIDDEN, AssignCoachMemberRelationResult.AssignRelationFailureReason.FORBIDDEN.toString(), "");
            }
        };
        return Response
                .status(response.status())
                .links(links)
                .entity(response)
                .build();
    }
}
