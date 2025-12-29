package adapter.in.Presenter.Member;

import adapter.in.DTOs.ErrorResponse;
import adapter.in.Links.member.MemberRegistrationLinks;
import domain.Results.RegisterUserResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

@ApplicationScoped
public class HttpRegisterMemberPresenter {
    public Response toHttp(RegisterUserResult result, UriInfo uriInfo) {
        if (result instanceof RegisterUserResult.Failure failure) {
            return getFailureResponse(failure, uriInfo);
        }
        if (result instanceof RegisterUserResult.Success success) {

            URI self = adapter.in.Links.member.MemberRegistrationLinks.getSelfUri(success.user().getId(), uriInfo);
            Link[] links = adapter.in.Links.member.MemberRegistrationLinks.getLinks(success.user().getId(), uriInfo);
            return Response.created(self).links(links).build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }


    private Response getFailureResponse(RegisterUserResult.Failure failure, UriInfo uriInfo) {
        Link[] links;
        ErrorResponse response = switch (failure.reason()) {
            case RegisterUserResult.UserRegisterFailureReason.INVALID_BIRTHDAY -> {
                links = MemberRegistrationLinks.getInvalidBirthdayLinks();
                yield new ErrorResponse(Response.Status.BAD_REQUEST, failure.reason().toString(), "");
            }
            case RegisterUserResult.UserRegisterFailureReason.USER_ALREADY_EXISTS -> {
                links = MemberRegistrationLinks.getUserAlreadyExistsLinks();
                yield new ErrorResponse(Response.Status.BAD_REQUEST, failure.reason().toString(), "");
            }
            case RegisterUserResult.UserRegisterFailureReason.FIELD_EMPTY -> {
                links = MemberRegistrationLinks.getFieldEmptyLinks();
                yield new ErrorResponse(Response.Status.BAD_REQUEST, failure.reason().toString(), "");
            }
            case RegisterUserResult.UserRegisterFailureReason.PASSWORD_TOO_WEAK -> {
                links = MemberRegistrationLinks.getPasswordTooWeakLinks();
                yield new ErrorResponse(Response.Status.BAD_REQUEST, failure.reason().toString(), "");
            }
            default -> {
                links = MemberRegistrationLinks.getUnexpectedLinks();
                yield new ErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, failure.reason().toString(), "Unexpected error");
            }
        };
        return Response
                .status(response.status())
                .links(links)
                .entity(response)
                .build();
    }
}
