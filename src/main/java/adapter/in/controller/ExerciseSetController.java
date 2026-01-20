package adapter.in.controller;

import adapter.in.Presenter.HttpWorkoutExerciseOperationPresenter;
import application.commands.exerciseSet.ExerciseSetFilter;
import application.port.in.exerciseSet.LoadExerciseSetsUseCase;
import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.ExerciseSet;
import jakarta.inject.Inject;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.time.LocalDateTime;

import static adapter.in.services.CacheExpirationFactory.get10sPrivateNoMustValidateExpiration;

@Path("/exerciseSets")
public class ExerciseSetController {
    @Inject
    HttpWorkoutExerciseOperationPresenter presenter;
    @Inject
    UriInfo uriInfo;
    @Inject
    LoadExerciseSetsUseCase loadExerciseSetsUseCase;
    @Context
    Request request;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getExerciseSets(
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
        ExerciseSetFilter filter = new ExerciseSetFilter(null, repsGreaterThan, repsLessThan, weightGreaterThan, weightLessThan, durationGreaterThan,
                durationLessThan, createdBefore, createdAfter);

        JPAWorkoutExerciseAdapterResult<ExerciseSet> result = loadExerciseSetsUseCase.loadExerciseSets(filter, page, size);

        Response.ResponseBuilder cachedResponse = presenter.evaluateCache(result, request);
        if(cachedResponse != null) return cachedResponse.cacheControl(get10sPrivateNoMustValidateExpiration()).build();

        return presenter.toHttp(result, uriInfo);
    }
}
