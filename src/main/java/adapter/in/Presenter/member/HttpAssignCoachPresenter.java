package adapter.in.Presenter.member;

import adapter.in.DTOs.ErrorResponse;
import adapter.in.DTOs.ResponseDTOs.CoachMemberResponse;
import adapter.in.Links.member.MemberAssignsCoachLinks;
import adapter.in.mapper.MemberMapper;
import domain.Results.AssignCoachMemberRelationResult;
import domain.Results.AssignCoachMemberRelationResult.AssignRelationFailureReason;
import domain.Results.AssignCoachMemberRelationResult.Failure;
import domain.Results.AssignCoachMemberRelationResult.Success;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

@ApplicationScoped
public class HttpAssignCoachPresenter {

    public Response toHttp(AssignCoachMemberRelationResult result, UriInfo uriInfo) {
        if (result instanceof Failure failure) {

            return getFailureResponse(failure, uriInfo);
        }
        if (result instanceof Success success) {
            CoachMemberResponse dto = MemberMapper.toDTO(success.coachMember());
            URI selfUri = MemberAssignsCoachLinks.getSelfUri(dto, uriInfo);
            Link[] links = MemberAssignsCoachLinks.getLinks(dto, uriInfo);
            return Response.created(selfUri).links(links).build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    private Response getFailureResponse(AssignCoachMemberRelationResult.Failure failure, UriInfo uriInfo) {
        Link[] links;
        ErrorResponse response = switch (failure.reason()) {
            case AssignRelationFailureReason.COACH_NOT_FOUND -> {
                links = MemberAssignsCoachLinks.getCoachNotFoundLinks(uriInfo);
                yield new ErrorResponse(Response.Status.NOT_FOUND, AssignRelationFailureReason.COACH_NOT_FOUND.toString(), "");
            }
            case AssignRelationFailureReason.MEMBER_NOT_FOUND -> {
                links = MemberAssignsCoachLinks.getMemberNotFoundLinks(uriInfo);
                yield new ErrorResponse(Response.Status.NOT_FOUND, AssignRelationFailureReason.MEMBER_NOT_FOUND.toString(), "");
            }
            case AssignRelationFailureReason.RELATION_ALREADY_EXISTS -> {
                links = MemberAssignsCoachLinks.getRelationAlreadyExistsLinks(uriInfo);
                yield new ErrorResponse(Response.Status.CONFLICT, AssignRelationFailureReason.RELATION_ALREADY_EXISTS.toString(), "");
            }
            case AssignRelationFailureReason.UNAUTHORIZED -> {
                links = MemberAssignsCoachLinks.getUnauthorizedLinks(uriInfo);
                yield new ErrorResponse(Response.Status.UNAUTHORIZED, AssignRelationFailureReason.UNAUTHORIZED.toString(), "");
            }
            case  AssignCoachMemberRelationResult.AssignRelationFailureReason.FORBIDDEN -> {
                links = MemberAssignsCoachLinks.getForbiddenLinks(uriInfo);
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
