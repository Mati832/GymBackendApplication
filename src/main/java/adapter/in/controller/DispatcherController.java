package adapter.in.controller;

import adapter.in.Presenter.HttpDispatcherPresenter;
import adapter.in.services.JwtAdapter;
import application.commands.AuthenticatedUser;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/")
public class DispatcherController {

    @Inject
    JwtAdapter jwtService;
    @Inject
    HttpDispatcherPresenter httpDispatcherPresenter;
    @Context
    UriInfo uriInfo;

    @Path("")
    @GET
    public Response getDispatcher(@HeaderParam("Authorization") String authHeader){
        AuthenticatedUser user = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            user = jwtService.validateToken(authHeader.substring(7));
        }

        return httpDispatcherPresenter.toHttp(uriInfo,user);
    }
}
