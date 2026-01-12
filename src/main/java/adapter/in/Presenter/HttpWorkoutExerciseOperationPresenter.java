package adapter.in.Presenter;

import adapter.in.DTOs.ResponseDTOs.PaginatedResponseDTO;
import domain.Results.JPAWorkoutExerciseAdapterResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@ApplicationScoped
public class HttpWorkoutExerciseOperationPresenter {
    //ToDo: uriInfo and HATEOAS
    public <T> Response toHttp(JPAWorkoutExerciseAdapterResult<T> result, UriInfo uriInfo) {
        return switch (result) {
            case JPAWorkoutExerciseAdapterResult.Success<T> s ->
                    Response.ok(s.value()).build();

            case JPAWorkoutExerciseAdapterResult.Created<T> c ->
                    Response.status(Response.Status.CREATED).entity(c.value()).build();

            case JPAWorkoutExerciseAdapterResult.Updated<T> u ->
                    Response.ok(u.value()).build();

            case JPAWorkoutExerciseAdapterResult.Deleted<T> d ->
                    d.success() ? Response.noContent().build() : Response.status(400).build();

            case JPAWorkoutExerciseAdapterResult.Paginated<T> p ->
                    Response.ok(new PaginatedResponseDTO<>(p.values(), p.page(), p.size(), p.totalPageCount())).build();

            case JPAWorkoutExerciseAdapterResult.Failure<T> f ->
                    Response.status(f.reason().getStatus()).entity(f.reason().name()).build();
        };
    }
}