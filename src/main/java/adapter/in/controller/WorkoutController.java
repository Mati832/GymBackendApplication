package adapter.in.controller;

import adapter.in.Presenter.HttpWorkoutExerciseOperationPresenter;
import application.commands.workout.WorkoutFilter;
import application.port.in.workout.*;
import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.Workout;
import jakarta.inject.Inject;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.time.LocalDateTime;

@Path("/workouts")
public class WorkoutController {
    @Inject
    HttpWorkoutExerciseOperationPresenter presenter;
    @Inject
    UriInfo uriInfo;
    @Inject
    LoadWorkoutsUseCase loadWorkoutsUseCase;
    @Context
    Request request;

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

        Response cachedResponse = presenter.evaluateCache(result, request);
        if(cachedResponse != null) return cachedResponse;

        return presenter.toHttp(result, uriInfo);
    }
}
