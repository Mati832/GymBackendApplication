package adapter.in.controller;

import adapter.in.Presenter.HttpWorkoutExerciseOperationPresenter;
import application.commands.exercise.ExerciseFilter;
import application.port.in.exercise.*;
import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.Exercise;
import jakarta.inject.Inject;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.time.LocalDateTime;

@Path("/exercises")
public class ExerciseController {
    @Inject
    HttpWorkoutExerciseOperationPresenter presenter;
    @Inject
    UriInfo uriInfo;
    @Inject
    LoadExercisesUseCase loadExercisesUseCase;

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
}
