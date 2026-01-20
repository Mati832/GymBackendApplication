package adapter.in.controller;

import adapter.in.Presenter.HttpWorkoutExerciseOperationPresenter;
import application.commands.exercise.ExerciseFilter;
import application.port.in.exercise.*;
import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.Exercise;
import jakarta.inject.Inject;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.time.LocalDateTime;

import static adapter.in.services.CacheExpirationFactory.get10sPrivateNoMustValidateExpiration;

@Path("/exercises")
public class ExerciseController {
    @Inject
    HttpWorkoutExerciseOperationPresenter presenter;
    @Inject
    UriInfo uriInfo;
    @Inject
    LoadExercisesUseCase loadExercisesUseCase;
    @Context
    Request request;

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

        Response.ResponseBuilder cachedResponse = presenter.evaluateCache(result, request);
        if(cachedResponse != null) return cachedResponse.cacheControl(get10sPrivateNoMustValidateExpiration()).build();

        return presenter.toHttp(result, uriInfo);
    }
}
