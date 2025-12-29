package adapter.in.Presenter;

import adapter.in.DTOs.ErrorResponse;
import adapter.in.DTOs.ResponseDTOs.LoginUserResponse;
import adapter.in.Links.LoginUserLinks;
import adapter.in.services.JwtAdapter;
import domain.Results.LoginUserResult;
import domain.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@ApplicationScoped
public class HttpLoginUserPresenter {

    @Inject
    JwtAdapter jwtService;

    public Response toHttp(LoginUserResult result, UriInfo uriInfo) {
        if (result instanceof LoginUserResult.Failure failure) {
            return getErrorResponse(failure, uriInfo);
        }
        if (result instanceof LoginUserResult.Success success) {
            User res = success.user();
            var response = new LoginUserResponse(res.getId(), res.getEmail(), res.getFirstName(), res.getLastName(), res.getGender(), res.getBornOn(), res.getCreatedAt());
            Link[] links = LoginUserLinks.getLinks(response, uriInfo);
            String jwt = jwtService.generateToken(res.getId().toString());
            return Response
                    .ok(response)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                    .links(links)
                    .build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    private Response getErrorResponse(LoginUserResult.Failure failure, UriInfo uriInfo) {
        Link[] links;
        ErrorResponse response = switch (failure.reason()) {
            case LoginUserResult.LoginFailureReason.USER_NOT_FOUND -> {
                links = LoginUserLinks.getUserNotFoundLinks(uriInfo);
                yield new ErrorResponse(Response.Status.NOT_FOUND, LoginUserResult.LoginFailureReason.USER_NOT_FOUND.name(), "");
            }
            case LoginUserResult.LoginFailureReason.WRONG_PASSWORD -> {
                links = LoginUserLinks.getWrongPasswordLinks(uriInfo);
                yield new ErrorResponse(Response.Status.UNAUTHORIZED, LoginUserResult.LoginFailureReason.WRONG_PASSWORD.name(), "");
            }
        };
        return Response
                .status(response.status())
                .links(links)
                .entity(response)
                .build();
    }

}
