package adapter.in.controller;

import adapter.in.DTOs.RequestDTOs.exercise.ExerciseRequest;
import adapter.in.DTOs.RequestDTOs.exerciseSet.ExerciseSetRequest;
import adapter.in.DTOs.RequestDTOs.workout.WorkoutRequest;
import adapter.in.Presenter.HttpWorkoutExerciseOperationPresenter;
import adapter.in.services.JwtAdapter;
import application.commands.exercise.ExerciseFilter;
import application.commands.exerciseSet.ExerciseSetFilter;
import application.commands.workout.WorkoutFilter;
import application.port.in.exercise.*;
import application.port.in.exerciseSet.*;
import application.port.in.workout.*;
import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.Exercise;
import domain.model.ExerciseSet;
import domain.model.Workout;
import jakarta.inject.Inject;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.time.LocalDateTime;

import static adapter.in.mapper.ExerciseSetMapper.toDomain;
import static adapter.in.mapper.ExerciseMapper.toDomain;
import static adapter.in.mapper.WorkoutMapper.toDomain;
import static adapter.in.services.CacheExpirationFactory.get10sPrivateNoMustValidateExpiration;

@Path("/users")
public class UserController {
    @Inject
    JwtAdapter jwtAdapter;
    @Inject
    UriInfo uriInfo;
    @Inject
    HttpWorkoutExerciseOperationPresenter presenter;
    @Context
    Request request;

    //workouts use cases
    @Inject
    LoadWorkoutsUseCase loadWorkoutsUseCase;
    @Inject
    LoadWorkoutByIdUseCase loadWorkoutByIdUseCase;
    @Inject
    AddWorkoutToUserUseCase addWorkoutToUserUseCase;
    @Inject
    DeleteWorkoutInUserUseCase deleteWorkoutInUserUseCase;
    @Inject
    EditWorkoutInUserUseCase editWorkoutInUserUseCase;


    //exercise use cases
    @Inject
    LoadExercisesUseCase loadExercisesUseCase;
    @Inject
    LoadExerciseByIdUseCase loadExerciseByIdUseCase;
    @Inject
    AddExerciseToUserUseCase addExerciseToUserUseCase;
    @Inject
    AddExerciseToWorkoutUseCase addExerciseToWorkoutUseCase;
    @Inject
    DeleteExerciseInUserUseCase deleteExerciseInUserUseCase;
    @Inject
    DeleteExerciseInWorkoutUseCase deleteExerciseInWorkoutUseCase;
    @Inject
    EditExerciseUseCase editExerciseUseCase;


    //exerciseSets use cases
    @Inject
    LoadExerciseSetsUseCase loadExerciseSetsUseCase;
    @Inject
    LoadExerciseSetByIdUseCase loadExerciseSetByIdUseCase;
    @Inject
    AddExerciseSetToExerciseUseCase addExerciseSetToExerciseUseCase;
    @Inject
    DeleteExerciseSetInExerciseUseCase deleteExerciseSetInExerciseUseCase;
    @Inject
    EditExerciseSetUseCase editExerciseSetUseCase;


    //WORKOUTS IN USER:

    //GET

    @Path("/{userID}/workouts")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWorkouts(
            @PathParam("userID") Long userId,
            @QueryParam("name") String name,
            @QueryParam("createdBefore") LocalDateTime createdBefore,
            @QueryParam("createdAfter") LocalDateTime createdAfter,
            @PositiveOrZero @DefaultValue("0") @QueryParam("page") int page,
            @PositiveOrZero @DefaultValue("10") @QueryParam("size") int size
    ) {
        WorkoutFilter filter = new WorkoutFilter(userId, name, createdBefore, createdAfter);
        JPAWorkoutExerciseAdapterResult<Workout> result = loadWorkoutsUseCase.loadWorkouts(filter, page, size);

        Response.ResponseBuilder cachedResponse = presenter.evaluateCache(result, request);
        if(cachedResponse != null) return cachedResponse.cacheControl(get10sPrivateNoMustValidateExpiration()).build();

        return presenter.toHttp(result, uriInfo);
    }

    @Path("/{userID}/workouts/{workoutID}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWorkout(
            @PathParam("workoutID") Long workoutId
    ){
        JPAWorkoutExerciseAdapterResult<Workout> result = loadWorkoutByIdUseCase.loadWorkoutById(workoutId);

        Response.ResponseBuilder cachedResponse = presenter.evaluateCache(result, request);
        if(cachedResponse != null) return cachedResponse.cacheControl(get10sPrivateNoMustValidateExpiration()).build();

        return  presenter.toHttp(result, uriInfo);
    }


    //POST

    @Path("/{userID}/workouts")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createWorkout(
            WorkoutRequest workoutRequest,
            @HeaderParam("Authorization")String authHeader
    ) {
        JPAWorkoutExerciseAdapterResult<Workout> result =
                addWorkoutToUserUseCase.addWorkoutToUser(jwtAdapter.resolveJWTtoId(authHeader), toDomain(workoutRequest));
        return presenter.toHttp(result, uriInfo);
    }


    //PUT

    @Path("/{userID}/workouts/{workoutID}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateWorkout(
            @PathParam("workoutID") Long workoutId,
            @HeaderParam("Authorization")String authHeader,
            WorkoutRequest workoutRequest
    ){
        Response.ResponseBuilder cachedResponse = presenter.evaluateCache(loadWorkoutByIdUseCase.loadWorkoutById(workoutId), request);
        if(cachedResponse != null) return cachedResponse.build();

        Workout wRequest = toDomain(workoutRequest);
        Long requestedBy = jwtAdapter.resolveJWTtoId(authHeader);
        wRequest.setCreatedByUserId(requestedBy);
        JPAWorkoutExerciseAdapterResult<Workout> result = editWorkoutInUserUseCase.editWorkoutInUser(workoutId, wRequest);
        return presenter.toHttp(result, uriInfo);
    }


    //DELETE

    @Path("/{userID}/workouts/{workoutID}")
    @DELETE
    public Response deleteWorkout(
            @PathParam("workoutID") Long workoutId,
            @HeaderParam("Authorization")String authHeader
    ){
        Response.ResponseBuilder cachedResponse = presenter.evaluateCache(loadWorkoutByIdUseCase.loadWorkoutById(workoutId), request);
        if(cachedResponse != null) return cachedResponse.build();

        JPAWorkoutExerciseAdapterResult<Void> result = deleteWorkoutInUserUseCase.deleteWorkoutInUser(jwtAdapter.resolveJWTtoId(authHeader), workoutId);
        return presenter.toHttp(result, uriInfo);
    }



    //EXERCISES:


    //EXERCISES IN WORKOUTS:

    //GET

    @Path("/{userID}/workouts/{workoutID}/exercises")
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

        Response.ResponseBuilder cachedResponse = presenter.evaluateCache(result, request);
        if(cachedResponse != null) return cachedResponse.cacheControl(get10sPrivateNoMustValidateExpiration()).build();

        return presenter.toHttp(result, uriInfo);
    }

    @Path("/{userID}/workouts/{workoutID}/exercises/{exerciseID}")
    @GET
    public Response getExerciseInWorkout(
            @PathParam("exerciseID") Long exerciseId
    ){
        JPAWorkoutExerciseAdapterResult<Exercise> result = loadExerciseByIdUseCase.loadExerciseById(exerciseId);

        Response.ResponseBuilder cachedResponse = presenter.evaluateCache(result, request);
        if(cachedResponse != null) return cachedResponse.cacheControl(get10sPrivateNoMustValidateExpiration()).build();

        return presenter.toHttp(result, uriInfo);
    }


    //POST

    @Path("/{userID}/workouts/{workoutID}/exercises")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createExerciseToWorkout(
            @PathParam("workoutID") Long workoutId,
            @HeaderParam("Authorization")String authHeader,
            ExerciseRequest exerciseRequest
    ){
        Exercise eRequest = toDomain(exerciseRequest);
        Long requestedBy = jwtAdapter.resolveJWTtoId(authHeader);
        eRequest.setCreatedByUserId(requestedBy);
        JPAWorkoutExerciseAdapterResult<Exercise> result = addExerciseToWorkoutUseCase.addExerciseToWorkout(workoutId, eRequest);
        return presenter.toHttp(result, uriInfo);
    }


    //PUT

    @Path("/{userID}/workouts/{workoutID}/exercises/{exerciseID}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateExerciseInWorkout(
            @PathParam("exerciseID") Long exerciseId,
            @HeaderParam("Authorization") String authHeader,
            ExerciseRequest exerciseRequest
    ){
        Response.ResponseBuilder cachedResponse = presenter.evaluateCache(loadExerciseByIdUseCase.loadExerciseById(exerciseId), request);
        if(cachedResponse != null) return cachedResponse.build();

        Exercise eRequest = toDomain(exerciseRequest);
        Long requestedBy = jwtAdapter.resolveJWTtoId(authHeader);
        eRequest.setCreatedByUserId(requestedBy);

        JPAWorkoutExerciseAdapterResult<Exercise> result = editExerciseUseCase.editExercise(exerciseId, eRequest);
        return presenter.toHttp(result, uriInfo);
    }


    //DELETE

    @Path("/{userID}/workouts/{workoutID}/exercises/{exerciseID}")
    @DELETE
    public Response deleteExerciseInWorkout(
            @PathParam("workoutID") Long workoutId,
            @PathParam("exerciseID")  Long exerciseId,
            @HeaderParam("Authorization") String authHeader
    ){
        Response.ResponseBuilder cachedResponse = presenter.evaluateCache(loadExerciseByIdUseCase.loadExerciseById(exerciseId), request);
        if(cachedResponse != null) return cachedResponse.build();

        JPAWorkoutExerciseAdapterResult<Void> result = deleteExerciseInWorkoutUseCase.deleteExerciseInWorkout(jwtAdapter.resolveJWTtoId(authHeader), workoutId, exerciseId);
        return presenter.toHttp(result, uriInfo);
    }




    //EXERCISES IN USER


    //GET

    @Path("/{userID}/exercises")
    @GET
    public Response getExercisesInUser(
            @QueryParam("name") String name,
            @QueryParam("createdBefore") LocalDateTime createdBefore,
            @QueryParam("createdAfter") LocalDateTime createdAfter,
            @PathParam("userID")  Long userId,
            @PositiveOrZero @DefaultValue("0") @QueryParam("page") int page,
            @PositiveOrZero @DefaultValue("10") @QueryParam("size") int size
    ){
        ExerciseFilter filter = new ExerciseFilter(userId, null, name,  createdBefore, createdAfter);
        JPAWorkoutExerciseAdapterResult<Exercise> result = loadExercisesUseCase.loadExercises(filter, page, size);

        Response.ResponseBuilder cachedResponse = presenter.evaluateCache(result, request);
        if(cachedResponse != null) return cachedResponse.cacheControl(get10sPrivateNoMustValidateExpiration()).build();

        return presenter.toHttp(result, uriInfo);
    }

    @Path("/{userID}/exercises/{exerciseID}")
    @GET
    public Response getExerciseInUser(
            @PathParam("exerciseID") Long exerciseId
    ){
        JPAWorkoutExerciseAdapterResult<Exercise> result = loadExerciseByIdUseCase.loadExerciseById(exerciseId);

        Response.ResponseBuilder cachedResponse = presenter.evaluateCache(result, request);
        if(cachedResponse != null) return cachedResponse.cacheControl(get10sPrivateNoMustValidateExpiration()).build();

        return presenter.toHttp(result, uriInfo);
    }


    //POST

    @Path("/{userID}/exercises")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createExerciseInUser(
            @HeaderParam("Authorization") String authHeader,
            ExerciseRequest request
    ){
        Exercise exercise = toDomain(request);
        Long requestedBy = jwtAdapter.resolveJWTtoId(authHeader);
        exercise.setCreatedByUserId(requestedBy);
        JPAWorkoutExerciseAdapterResult<Exercise> result = addExerciseToUserUseCase.addExerciseToUser(requestedBy, exercise);
        return presenter.toHttp(result, uriInfo);
    }


    //PUT

    @Path("/{userID}/exercises/{exerciseID}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateExerciseInUser(
            @PathParam("exerciseID") Long exerciseId,
            @HeaderParam("Authorization") String authHeader,
            ExerciseRequest exerciseRequest
    ){
        Response.ResponseBuilder cachedResponse = presenter.evaluateCache(loadExerciseByIdUseCase.loadExerciseById(exerciseId), request);
        if(cachedResponse != null) return cachedResponse.build();

        Exercise eRequest = toDomain(exerciseRequest);
        Long requestedBy = jwtAdapter.resolveJWTtoId(authHeader);
        eRequest.setCreatedByUserId(requestedBy);

        JPAWorkoutExerciseAdapterResult<Exercise> result = editExerciseUseCase.editExercise(exerciseId, eRequest);
        return presenter.toHttp(result, uriInfo);
    }


    //DELETE

    @Path("/{userID}/exercises/{exerciseID}")
    @DELETE
    public Response deleteExerciseInUser(
            @PathParam("exerciseID")  Long exerciseId,
            @HeaderParam("Authorization")   String authHeader
    ){
        Response.ResponseBuilder cachedResponse = presenter.evaluateCache(loadExerciseByIdUseCase.loadExerciseById(exerciseId), request);
        if(cachedResponse != null) return cachedResponse.build();

        JPAWorkoutExerciseAdapterResult<Void> result =
                deleteExerciseInUserUseCase.deleteExerciseInUser(jwtAdapter.resolveJWTtoId(authHeader), exerciseId);
        return presenter.toHttp(result, uriInfo);
    }




    //EXERCISE-SETS IN EXERCISE

    //GET

    @Path("/{userID}/exercises/{exerciseID}/exerciseSets")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getExerciseSetsInExercise(
            @PathParam("exerciseID") Long exerciseId,
            @QueryParam("repsGreaterThan") Integer repsGreaterThan,
            @QueryParam("repsLessThan") Integer repsLessThan,
            @QueryParam("weightGreaterThan") Double weightGreaterThan,
            @QueryParam("weightLessThan") Double weightLessThan,
            @QueryParam("durationGreaterThan") Long durationGreaterThan,
            @QueryParam("durationLessThan") Long durationLessThan,
            @QueryParam("createdBefore")LocalDateTime createdBefore,
            @QueryParam("createdAfter") LocalDateTime createdAfter,
            @PositiveOrZero @DefaultValue("0") @QueryParam("page") int page,
            @PositiveOrZero @DefaultValue("10") @QueryParam("size") int size
    ) {
        ExerciseSetFilter filter = new ExerciseSetFilter(exerciseId, repsGreaterThan, repsLessThan, weightGreaterThan, weightLessThan, durationGreaterThan,
                durationLessThan, createdBefore, createdAfter);

        JPAWorkoutExerciseAdapterResult<ExerciseSet> result = loadExerciseSetsUseCase.loadExerciseSets(filter, page, size);

        Response.ResponseBuilder cachedResponse = presenter.evaluateCache(result, request);
        if(cachedResponse != null) return cachedResponse.cacheControl(get10sPrivateNoMustValidateExpiration()).build();

        return presenter.toHttp(result, uriInfo);
    }

    @Path("/{userID}/exercises/{exerciseID}/exerciseSets/{exerciseSetID}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getExerciseSetById(
            @PathParam("exerciseSetID") Long exerciseSetId
    ) {
        JPAWorkoutExerciseAdapterResult<ExerciseSet> result = loadExerciseSetByIdUseCase.loadExerciseSetById(exerciseSetId);

        Response.ResponseBuilder cachedResponse = presenter.evaluateCache(result, request);
        if(cachedResponse != null) return cachedResponse.cacheControl(get10sPrivateNoMustValidateExpiration()).build();

        return presenter.toHttp(result, uriInfo);
    }


    //POST

    @Path("/{userID}/exercises/{exerciseID}/exerciseSets")
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


    //PUT

    @Path("/{userID}/exercises/{exerciseID}/exerciseSets/{exerciseSetID}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateExerciseSetInExercise(
            @PathParam("exerciseSetID") Long exerciseSetId,
            @HeaderParam("Authorization")  String authHeader,
            ExerciseSetRequest exerciseSetRequest
    ){
        Response.ResponseBuilder  cachedResponse = presenter.evaluateCache(loadExerciseSetByIdUseCase.loadExerciseSetById(exerciseSetId), request);
        if(cachedResponse != null) return cachedResponse.build();

        JPAWorkoutExerciseAdapterResult<ExerciseSet> result =
                editExerciseSetUseCase.editExerciseSet(jwtAdapter.resolveJWTtoId(authHeader), exerciseSetId, toDomain(exerciseSetRequest) );
        return presenter.toHttp(result, uriInfo);
    }


    //DELETE
    @Path("/{userID}/exercises/{exerciseID}/exerciseSets/{exerciseSetID}")
    @DELETE
    public Response deleteExerciseSetInExercise(
            @PathParam("exerciseID") Long exerciseId,
            @PathParam("exerciseSetID") Long exerciseSetId,
            @HeaderParam("Authorization") String  authHeader
    ){
        Response.ResponseBuilder  cachedResponse = presenter.evaluateCache(loadExerciseSetByIdUseCase.loadExerciseSetById(exerciseSetId), request);
        if(cachedResponse != null) return cachedResponse.build();

        JPAWorkoutExerciseAdapterResult<Void> result =
                deleteExerciseSetInExerciseUseCase.deleteExerciseSetInExercise(jwtAdapter.resolveJWTtoId(authHeader), exerciseId, exerciseSetId);
        return presenter.toHttp(result, uriInfo);
    }



    //EXERCISE-SETS IN EXERCISE WITH WORKOUT

    //GET

    @Path("/{userID}/workouts/{workoutID}/exercises/{exerciseID}/exerciseSets")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getExerciseSetsInExerciseWithWorkout(
            @PathParam("exerciseID") Long exerciseId,
            @QueryParam("repsGreaterThan") Integer repsGreaterThan,
            @QueryParam("repsLessThan") Integer repsLessThan,
            @QueryParam("weightGreaterThan") Double weightGreaterThan,
            @QueryParam("weightLessThan") Double weightLessThan,
            @QueryParam("durationGreaterThan") Long durationGreaterThan,
            @QueryParam("durationLessThan") Long durationLessThan,
            @QueryParam("createdBefore")LocalDateTime createdBefore,
            @QueryParam("createdAfter") LocalDateTime createdAfter,
            @PositiveOrZero @DefaultValue("0") @QueryParam("page") int page,
            @PositiveOrZero @DefaultValue("10") @QueryParam("size") int size
    ) {
        ExerciseSetFilter filter = new ExerciseSetFilter(exerciseId, repsGreaterThan, repsLessThan, weightGreaterThan, weightLessThan, durationGreaterThan,
                durationLessThan, createdBefore, createdAfter);

        JPAWorkoutExerciseAdapterResult<ExerciseSet> result = loadExerciseSetsUseCase.loadExerciseSets(filter, page, size);

        Response.ResponseBuilder cachedResponse = presenter.evaluateCache(result, request);
        if(cachedResponse != null) return cachedResponse.cacheControl(get10sPrivateNoMustValidateExpiration()).build();

        return presenter.toHttp(result, uriInfo);
    }

    @Path("/{userID}/workouts/{workoutID}/exercises/{exerciseID}/exerciseSets/{exerciseSetID}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getExerciseSetByIdWithWorkout(
            @PathParam("exerciseSetID") Long exerciseSetId
    ) {
        JPAWorkoutExerciseAdapterResult<ExerciseSet> result = loadExerciseSetByIdUseCase.loadExerciseSetById(exerciseSetId);

        Response.ResponseBuilder cachedResponse = presenter.evaluateCache(result, request);
        if(cachedResponse != null) return cachedResponse.cacheControl(get10sPrivateNoMustValidateExpiration()).build();

        return presenter.toHttp(result, uriInfo);
    }


    //POST

    @Path("/{userID}/workouts/{workoutID}/exercises/{exerciseID}/exerciseSets")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createExerciseSetInExerciseWithWorkout(
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


    //PUT

    @Path("/{userID}/workouts/{workoutID}/exercises/{exerciseID}/exerciseSets/{exerciseSetID}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateExerciseSetInExerciseWithWorkout(
            @PathParam("exerciseSetID") Long exerciseSetId,
            @HeaderParam("Authorization")  String authHeader,
            ExerciseSetRequest exerciseSetRequest
    ){
        Response.ResponseBuilder cachedResponse = presenter.evaluateCache(loadExerciseSetByIdUseCase.loadExerciseSetById(exerciseSetId), request);
        if(cachedResponse != null) return cachedResponse.build();

        JPAWorkoutExerciseAdapterResult<ExerciseSet> result =
                editExerciseSetUseCase.editExerciseSet(jwtAdapter.resolveJWTtoId(authHeader), exerciseSetId, toDomain(exerciseSetRequest) );
        return presenter.toHttp(result, uriInfo);
    }


    //DELETE
    @Path("/{userID}/workouts/{workoutID}/exercises/{exerciseID}/exerciseSets/{exerciseSetID}")
    @DELETE
    public Response deleteExerciseSetInExerciseWithWorkout(
            @PathParam("exerciseID") Long exerciseId,
            @PathParam("exerciseSetID") Long exerciseSetId,
            @HeaderParam("Authorization") String  authHeader
    ){
        Response.ResponseBuilder cachedResponse = presenter.evaluateCache(loadExerciseSetByIdUseCase.loadExerciseSetById(exerciseSetId), request);
        if(cachedResponse != null) return cachedResponse.build();

        JPAWorkoutExerciseAdapterResult<Void> result =
                deleteExerciseSetInExerciseUseCase.deleteExerciseSetInExercise(jwtAdapter.resolveJWTtoId(authHeader), exerciseId, exerciseSetId);
        return presenter.toHttp(result, uriInfo);
    }
}