package adapter.in.controller;

import adapter.in.DTOs.RequestDTOs.coach.AssignMemberDTO;
import adapter.in.DTOs.RequestDTOs.coach.RegisterCoachDTO;
import adapter.in.Presenter.coach.HttpAssignMemberPresenter;
import adapter.in.Presenter.coach.HttpCoachAssignsWorkoutPresenter;
import adapter.in.Presenter.coach.HttpRegisterCoachPresenter;
import adapter.in.mapper.CoachMapper;
import adapter.in.services.JwtAdapter;
import application.commands.AuthenticatedUser;
import application.commands.coach.AssignWorkoutCommand;
import application.port.in.AssignCoachMemberRelationUseCase;
import application.port.in.coach.AssignWorkoutUseCase;
import application.port.in.coach.CoachRegistrationUseCase;
import domain.Results.AssignCoachMemberRelationResult;
import domain.Results.RegisterUserResult;
import domain.Results.coach.AssignWorkoutResult;
import jakarta.inject.Inject;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/users/coaches")
public class CoachWebController {

    @Inject
    CoachRegistrationUseCase coachRegistrationUseCase;
    @Inject
    HttpRegisterCoachPresenter presenter;
    @Inject
    JwtAdapter jwtService;
    @Inject
    AssignCoachMemberRelationUseCase assignMember;
    @Inject
    HttpAssignMemberPresenter httpAssignMemberPresenter;
    @Inject
    AssignWorkoutUseCase assignWorkout;
    @Inject
    HttpCoachAssignsWorkoutPresenter  httpCoachAssignsWorkoutPresenter;

    @Context
    UriInfo uriInfo;

    @POST//nicht put weil email nicht die id ist
    @Path("/register")
    public Response register(RegisterCoachDTO request) {
        RegisterUserResult result = coachRegistrationUseCase.registerCoach(CoachMapper.toDomain(request));
        return presenter.toHttp(result, uriInfo);
    }

    @POST
    @Path("/assign")
    public Response assignMember(@HeaderParam("Authorization") String authHeader, AssignMemberDTO assignMemberDTO) {
        //validiert Token. wenn nicht valide ist requestedBy null -> muss dann in application service überprüft werden
        application.commands.AuthenticatedUser requestedBy = null;
        if(authHeader!=null && authHeader.startsWith("Bearer ")) {
            requestedBy= jwtService.validateToken(authHeader.substring(7));
        }

        AssignCoachMemberRelationResult result = assignMember.assign(CoachMapper.toDomain(assignMemberDTO, requestedBy));
        return httpAssignMemberPresenter.toHttp(result, uriInfo);
    }

    @POST
    @Path("{coachID}/members/{memberID}/workouts/{workoutID}")
    public Response assignWorkout(@HeaderParam("Authorization")String authHeader, @PathParam("coachID") Long coachId, @PathParam("memberID") Long memberID, @PathParam("workoutID")Long workoutID) {
        AuthenticatedUser requestedBy=null;
        if(authHeader!=null && authHeader.startsWith("Bearer ")) {
            requestedBy= jwtService.validateToken(authHeader.substring(7));
        }
        AssignWorkoutResult result = assignWorkout.assign(new AssignWorkoutCommand(requestedBy, coachId, memberID, workoutID));
        return httpCoachAssignsWorkoutPresenter.toHttp(result, uriInfo);
    }
}
