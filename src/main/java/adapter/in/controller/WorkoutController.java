package adapter.in.controller;

import adapter.in.DTOs.RequestDTOs.exercise.ExerciseRequest;
import adapter.in.DTOs.RequestDTOs.workout.WorkoutRequest;
import adapter.in.Presenter.HttpWorkoutExerciseOperationPresenter;
import adapter.in.services.JwtAdapter;
import application.commands.exercise.ExerciseFilter;
import application.commands.workout.WorkoutFilter;
import application.port.in.exercise.AddExerciseToWorkoutUseCase;
import application.port.in.exercise.DeleteExerciseInWorkoutUseCase;
import application.port.in.exercise.LoadExercisesUseCase;
import application.port.in.workout.*;
import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.Exercise;
import domain.model.Workout;
import jakarta.inject.Inject;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.time.LocalDateTime;

import static adapter.in.mapper.ExerciseMapper.toDomain;
import static adapter.in.mapper.WorkoutMapper.toDomain;

@Path("/workouts")
public class WorkoutController {
    @Inject
    JwtAdapter jwtAdapter;
    @Inject
    HttpWorkoutExerciseOperationPresenter presenter;
    @Inject
    UriInfo uriInfo;
    @Inject
    LoadWorkoutsUseCase loadWorkoutsUseCase;
    @Inject
    LoadWorkoutByIdUseCase loadWorkoutByIdUseCase;
    @Inject
    LoadExercisesUseCase  loadExercisesUseCase;
    @Inject
    AddWorkoutToUserUseCase addWorkoutToUserUseCase;
    @Inject
    AddExerciseToWorkoutUseCase  addExerciseToWorkoutUseCase;
    @Inject
    DeleteExerciseInWorkoutUseCase deleteExerciseInWorkoutUseCase;
    @Inject
    EditWorkoutInUserUseCase  editWorkoutInUserUseCase;
    @Inject
    DeleteWorkoutInUserUseCase deleteWorkoutInUserUseCase;

    //WORKOUT

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWorkouts(
            @QueryParam("name") String name,
            @QueryParam("createdBefore")LocalDateTime createdBefore,
            @QueryParam("createdAfter") LocalDateTime createdAfter,
            @PositiveOrZero @DefaultValue("0") @QueryParam("page") int page,
            @PositiveOrZero @DefaultValue("10") @QueryParam("size") int size
            ) {
        WorkoutFilter filter = new WorkoutFilter(null, name, createdBefore, createdAfter);
        JPAWorkoutExerciseAdapterResult<Workout> result = loadWorkoutsUseCase.loadWorkouts(filter, page, size);
        return presenter.toHttp(result, uriInfo);
    }

    @Path("/{workoutID}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWorkout(
            @PathParam("workoutID") Long workoutId
    ){
        JPAWorkoutExerciseAdapterResult<Workout> result = loadWorkoutByIdUseCase.loadWorkoutById(workoutId);
        return  presenter.toHttp(result, uriInfo);
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createWorkout(
            WorkoutRequest workoutRequest,
            @HeaderParam("Authorization")String authHeader
    ) {
        Long requestedBy = jwtAdapter.resolveJWTtoId(authHeader);
        //usecase
        JPAWorkoutExerciseAdapterResult<Workout> result = addWorkoutToUserUseCase.addWorkoutToUser(requestedBy, toDomain(workoutRequest));
        //hier dann presenter
        return presenter.toHttp(result, uriInfo);
    }

    @Path("/{workoutID}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateWorkout(
            @PathParam("workoutID") Long workoutId,
            @HeaderParam("Authorization")String authHeader,
            WorkoutRequest workoutRequest
    ){
        Workout wRequest = toDomain(workoutRequest);
        Long requestedBy = jwtAdapter.resolveJWTtoId(authHeader);
        wRequest.setCreatedByUserId(requestedBy);
        JPAWorkoutExerciseAdapterResult<Workout> result = editWorkoutInUserUseCase.editWorkoutInUser(workoutId, wRequest);
        return presenter.toHttp(result, uriInfo);
    }

    @Path("/{workoutID}")
    @DELETE
    public Response deleteWorkout(
            @PathParam("workoutID") Long workoutId,
            @HeaderParam("Authorization")String authHeader
    ){
        JPAWorkoutExerciseAdapterResult<Void> result = deleteWorkoutInUserUseCase.deleteWorkoutInUser(jwtAdapter.resolveJWTtoId(authHeader), workoutId);
        return presenter.toHttp(result, uriInfo);
    }

    //EXERCISE IN WORKOUT

    @Path("/{workoutID}/exercises")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getExercisesInWorkout(
            @PathParam("workoutID") Long workoutId,
            @QueryParam("name") String name,
            @QueryParam("createdBefore") LocalDateTime createdBefore,
            @QueryParam("createdAfter") LocalDateTime createdAfter,
            @PositiveOrZero @DefaultValue("0") @QueryParam("page") int page,
            @PositiveOrZero @DefaultValue("10") @QueryParam("size") int size
    ){
        ExerciseFilter filter = new ExerciseFilter(null, workoutId, name, createdBefore, createdAfter);
        JPAWorkoutExerciseAdapterResult<Exercise> result = loadExercisesUseCase.loadExercises(filter, page, size);
        return presenter.toHttp(result, uriInfo);
    }

    @Path("/{workoutID}/exercises")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createExerciseToWorkout(
            @PathParam("workoutID") Long workoutId,
            @HeaderParam("Authorization")String authHeader,
            ExerciseRequest request
    ){
        //authentifizierung und autorisierung ob der nutzer auch das workout erstellt hat.
        Exercise eRequest = toDomain(request);
        Long requestedBy = jwtAdapter.resolveJWTtoId(authHeader);
        eRequest.setCreatedByUserId(requestedBy);
        JPAWorkoutExerciseAdapterResult<Exercise> result = addExerciseToWorkoutUseCase.addExerciseToWorkout(workoutId, eRequest);
        //presenter
        return presenter.toHttp(result, uriInfo);
    }


    @Path("/{workoutID}/exercises/{exerciseID}")
    @DELETE
    public Response deleteExerciseInWorkout(
            @PathParam("workoutID") Long workoutId,
            @PathParam("exerciseID")  Long exerciseId,
            @HeaderParam("Authorization") String authHeader
    ){
        JPAWorkoutExerciseAdapterResult<Void> result = deleteExerciseInWorkoutUseCase.deleteExerciseInWorkout(jwtAdapter.resolveJWTtoId(authHeader), workoutId, exerciseId);
        return presenter.toHttp(result, uriInfo);
    }
}
