package adapter.in.Presenter.coach;

import adapter.in.DTOs.ErrorResponse;
import adapter.in.Links.coach.CoachRegistrationLinks;
import domain.Results.RegisterUserResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

@ApplicationScoped
public class HttpRegisterCoachPresenter {
    public Response toHttp(RegisterUserResult result, UriInfo uriInfo) {
        if (result instanceof RegisterUserResult.Failure failure) {
            return getFailureResponse(failure, uriInfo);
        }
        if (result instanceof RegisterUserResult.Success success) {

            URI self = CoachRegistrationLinks.getSelfUri(success.user().getId(), uriInfo);
            Link[] links = CoachRegistrationLinks.getLinks(success.user().getId(), uriInfo);
            return Response.created(self).links(links).build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    private Response getFailureResponse(RegisterUserResult.Failure failure, UriInfo uriInfo) {
        Link[] links;
        ErrorResponse response = switch (failure.reason()) {
            case RegisterUserResult.UserRegisterFailureReason.INVALID_BIRTHDAY -> {
                links = CoachRegistrationLinks.getInvalidBirthdayLinks();
                yield new ErrorResponse(Response.Status.BAD_REQUEST, failure.reason().toString(), "");
            }
            case RegisterUserResult.UserRegisterFailureReason.USER_ALREADY_EXISTS -> {
                links = CoachRegistrationLinks.getUserAlreadyExistsLinks();
                yield new ErrorResponse(Response.Status.BAD_REQUEST, failure.reason().toString(), "");
            }
            case RegisterUserResult.UserRegisterFailureReason.FIELD_EMPTY -> {
                links = CoachRegistrationLinks.getFieldEmptyLinks();
                yield new ErrorResponse(Response.Status.BAD_REQUEST, failure.reason().toString(), "");
            }
            case RegisterUserResult.UserRegisterFailureReason.PASSWORD_TOO_WEAK -> {
                links = CoachRegistrationLinks.getPasswordTooWeakLinks();
                yield new ErrorResponse(Response.Status.BAD_REQUEST, failure.reason().toString(), "");
            }
            case RegisterUserResult.CoachRegisterFailureReason.INVALID_LICENSE -> {
                links = CoachRegistrationLinks.getInvalidLicenseLinks();
                yield new ErrorResponse(Response.Status.BAD_REQUEST, failure.reason().toString(), "");
            }
            default -> {
                links = CoachRegistrationLinks.getUnexpectedLinks();
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
