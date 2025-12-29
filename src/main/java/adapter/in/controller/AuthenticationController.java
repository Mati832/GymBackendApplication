package adapter.in.controller;

import adapter.in.DTOs.RequestDTOs.LoginUserDTO;
import adapter.in.Presenter.HttpLoginUserPresenter;
import application.commands.UserLoginCommand;
import application.port.in.UserLoginUseCase;
import domain.Results.LoginUserResult;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/auth")
public class AuthenticationController {

    @Inject
    UserLoginUseCase userLoginUseCase;

    @Inject
    HttpLoginUserPresenter presenter;

    @Context
    UriInfo uriInfo;

    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @Path("/login")
    public Response login(LoginUserDTO request) {
        LoginUserResult result = userLoginUseCase.loginUser(new UserLoginCommand(request.email(), request.password()));
        return presenter.toHttp(result, uriInfo);
    }
}
