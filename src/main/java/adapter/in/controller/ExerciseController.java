package adapter.in.controller;

import adapter.in.DTOs.RequestDTOs.exercise.ExerciseRequest;
import adapter.in.DTOs.RequestDTOs.exerciseSet.ExerciseSetRequest;
import adapter.in.Presenter.HttpWorkoutExerciseOperationPresenter;
import adapter.in.services.JwtAdapter;
import application.commands.exercise.ExerciseFilter;
import application.port.in.exercise.*;
import application.port.in.exerciseSet.AddExerciseSetToExerciseUseCase;
import application.port.in.exerciseSet.DeleteExerciseSetInExerciseUseCase;
import application.port.in.exerciseSet.EditExerciseSetUseCase;
import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.Exercise;
import domain.model.ExerciseSet;
import jakarta.inject.Inject;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.time.LocalDateTime;

import static adapter.in.mapper.ExerciseSetMapper.toDomain;
import static adapter.in.mapper.ExerciseMapper.toDomain;

@Path("/exercises")
public class ExerciseController {
    @Inject
    JwtAdapter jwtAdapter;
    @Inject
    HttpWorkoutExerciseOperationPresenter presenter;
    @Inject
    UriInfo uriInfo;
    @Inject
    LoadExercisesUseCase loadExercisesUseCase;
    @Inject
    LoadExerciseByIdUseCase loadExerciseByIdUseCase;
    @Inject
    EditExerciseUseCase editExerciseUseCase;
    @Inject
    AddExerciseToUserUseCase addExerciseToUserUseCase;
    @Inject
    AddExerciseSetToExerciseUseCase  addExerciseSetToExerciseUseCase;
    @Inject
    DeleteExerciseInUserUseCase  deleteExerciseInUserUseCase;
    @Inject
    EditExerciseSetUseCase  editExerciseSetUseCase;
    @Inject
    DeleteExerciseSetInExerciseUseCase  deleteExerciseSetInExerciseUseCase;

    //EXERCISES

    @GET
    public Response getExercises(
            @QueryParam("name") String name,
            @QueryParam("createdBefore") LocalDateTime createdBefore,
            @QueryParam("createdAfter") LocalDateTime createdAfter,
            @PositiveOrZero @DefaultValue("0") @QueryParam("page") int page,
            @PositiveOrZero @DefaultValue("10") @QueryParam("size") int size
    ){
        ExerciseFilter filter = new ExerciseFilter(null, null, name,  createdBefore, createdAfter);
        JPAWorkoutExerciseAdapterResult<Exercise> result = loadExercisesUseCase.loadExercises(filter, page, size);
        return presenter.toHttp(result, uriInfo);
    }

    @Path("/{exerciseID}")
    @GET
    public Response getExercise(
            @PathParam("exerciseID") Long exerciseId
    ){
        JPAWorkoutExerciseAdapterResult<Exercise> result = loadExerciseByIdUseCase.loadExerciseById(exerciseId);
        return presenter.toHttp(result, uriInfo);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createExerciseInUser(
            @HeaderParam("Authorization") String authHeader,
            ExerciseRequest request
    ){
        //authentifizierung
        Exercise exercise = toDomain(request);
        Long requestedBy = jwtAdapter.resolveJWTtoId(authHeader);
        exercise.setCreatedByUserId(requestedBy);
        //usecase
        JPAWorkoutExerciseAdapterResult<Exercise> result = addExerciseToUserUseCase.addExerciseToUser(requestedBy, exercise);
        //hier presenter
        return presenter.toHttp(result, uriInfo);
    }

    @Path("/{exerciseID}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateExercise(
            @PathParam("exerciseID") Long exerciseId,
            @HeaderParam("Authorization") String authHeader,
            ExerciseRequest request
    ){
        Exercise eRequest = toDomain(request);
        Long requestedBy = jwtAdapter.resolveJWTtoId(authHeader);
        eRequest.setCreatedByUserId(requestedBy);

        JPAWorkoutExerciseAdapterResult<Exercise> result = editExerciseUseCase.editExercise(exerciseId, eRequest);
        return presenter.toHttp(result, uriInfo);
    }

    @Path("/{exerciseID}")
    @DELETE
    public Response deleteExerciseInUser(
            @PathParam("exerciseID")  Long exerciseId,
            @HeaderParam("Authorization")   String authHeader
    ){
        JPAWorkoutExerciseAdapterResult<Void> result =
                deleteExerciseInUserUseCase.deleteExerciseInUser(jwtAdapter.resolveJWTtoId(authHeader), exerciseId);
        return presenter.toHttp(result, uriInfo);
    }

    //EXERCISESETS

    @Path("/{exerciseID}/exerciseSets")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createExerciseSetInExercise(
            @PathParam("exerciseID") Long exerciseId,
            @HeaderParam("Authorization")  String authHeader,
            ExerciseSetRequest request
    ){
        ExerciseSet eRequest = toDomain(request);
        eRequest.setBelongsToExercise(exerciseId);
        JPAWorkoutExerciseAdapterResult<ExerciseSet> result =
                addExerciseSetToExerciseUseCase.addExerciseSetToExercise(jwtAdapter.resolveJWTtoId(authHeader), exerciseId, eRequest);
        return presenter.toHttp(result, uriInfo);
    }

    @Path("/{exerciseID}/exerciseSets")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateExerciseSetInExercise(
            @PathParam("exerciseID") Long exerciseId,
            @HeaderParam("Authorization")  String authHeader,
            ExerciseSetRequest request
    ){
        JPAWorkoutExerciseAdapterResult<ExerciseSet> result =
                editExerciseSetUseCase.editExerciseSet(jwtAdapter.resolveJWTtoId(authHeader), exerciseId, toDomain(request) );
        return presenter.toHttp(result, uriInfo);
    }

    @Path("/{exerciseID}/exerciseSets/{exerciseSetID}")
    @DELETE
    public Response deleteExerciseSetInExercise(
            @PathParam("exerciseID") Long exerciseId,
            @PathParam("exerciseSetID") Long exerciseSetId,
            @HeaderParam("Authorization") String  authHeader
    ){
        JPAWorkoutExerciseAdapterResult<Void> result =
                deleteExerciseSetInExerciseUseCase.deleteExerciseSetInExercise(jwtAdapter.resolveJWTtoId(authHeader), exerciseId, exerciseSetId);
        return presenter.toHttp(result, uriInfo);
    }

}
