package adapter.in.controller;

import adapter.in.DTOs.RequestDTOs.member.AssignCoachDTO;
import adapter.in.DTOs.RequestDTOs.member.RegisterMemberDTO;
import adapter.in.Presenter.member.HttpAssignCoachPresenter;
import adapter.in.Presenter.member.HttpGetAssignedWorkoutsPresenter;
import adapter.in.Presenter.member.HttpRegisterMemberPresenter;
import adapter.in.mapper.MemberMapper;
import adapter.in.services.JwtAdapter;
import application.commands.PaginationCommand;
import application.commands.member.GetAssignedWorkoutsCommand;
import application.port.in.AssignCoachMemberRelationUseCase;
import application.port.in.member.MemberGetsAssignedWorkoutsUsecase;
import application.port.in.member.MemberRegistrationUseCase;
import domain.Results.AssignCoachMemberRelationResult;
import domain.Results.RegisterUserResult;
import domain.Results.member.AssignedWorkoutsResult;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/users/members")
public class MemberWebController {
    @Inject
    HttpAssignCoachPresenter httpAssignCoachPresenter;
    @Inject
    AssignCoachMemberRelationUseCase assignCoach;
    @Inject
    MemberRegistrationUseCase memberRegistration;
    @Inject
    HttpRegisterMemberPresenter registerMemberPresenter;
    @Inject
    MemberGetsAssignedWorkoutsUsecase getAssignedWorkoutsUsecase;
    @Inject
    HttpGetAssignedWorkoutsPresenter httpGetAssignedWorkoutsPresenter;

    @Context
    UriInfo uriInfo;

    @Inject
    JwtAdapter jwtService;


    @POST//nicht put weil email nicht die id ist
    @Path("/register")
    public Response register(RegisterMemberDTO request) {

        RegisterUserResult result = memberRegistration.registerMember(MemberMapper.toDomain(request));
        return registerMemberPresenter.toHttp(result, uriInfo);
    }

    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Path("/assign")
    public Response assignCoach(@HeaderParam("Authorization") String authHeader, AssignCoachDTO assignCoachDTO) {
        //validiert Token. wenn nicht valide ist requestedBy null -> muss dann in application service überprüft werden
        Long requestedBy = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            requestedBy = jwtService.validateToken(authHeader.substring(7));
        }
        AssignCoachMemberRelationResult result = assignCoach.assign(MemberMapper.toDomain(assignCoachDTO, requestedBy));
        return httpAssignCoachPresenter.toHttp(result, uriInfo);

    }

    @GET
    @Path("{id}/assigned-workouts")
    public Response getAssignedWorkouts(@HeaderParam("Authorization") String authHeader,
                                        @PathParam("id") Long memberId,
                                        @DefaultValue("0")@QueryParam("offset") int offset,
                                        @DefaultValue("20")@QueryParam("size") int size) {

        Long requestedBy = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            requestedBy = jwtService.validateToken(authHeader.substring(7));
        }
        PaginationCommand pagination=new PaginationCommand(offset,size);
        AssignedWorkoutsResult result = getAssignedWorkoutsUsecase.getWorkouts(new GetAssignedWorkoutsCommand(requestedBy, memberId, null, pagination));
        return httpGetAssignedWorkoutsPresenter.toHttp(result, uriInfo);
    }
}
