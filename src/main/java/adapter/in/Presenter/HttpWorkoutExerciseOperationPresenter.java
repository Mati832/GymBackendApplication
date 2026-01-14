package adapter.in.Presenter;

import adapter.in.DTOs.ResponseDTOs.PaginatedResponseDTO;
import adapter.in.mapper.ExerciseMapper;
import adapter.in.mapper.WorkoutMapper;
import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.Exercise;
import domain.model.ExerciseSet;
import domain.model.Workout;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static adapter.in.Links.LinkFactory.loginLink;
import static adapter.in.Links.WorkoutExerciseLinks.*;
import static adapter.in.mapper.ExerciseMapper.toResponse;
import static adapter.in.mapper.WorkoutMapper.toResponse;
import static adapter.in.mapper.ExerciseSetMapper.toResponse;

@ApplicationScoped
public class HttpWorkoutExerciseOperationPresenter {

    public HttpWorkoutExerciseOperationPresenter() {}
    public <T> Response toHttp(JPAWorkoutExerciseAdapterResult<T> result, UriInfo uriInfo) {
        List<Link> currentLinks = new ArrayList<>();
        return switch (result) {
            case JPAWorkoutExerciseAdapterResult.Success<T> s ->
                    Response.ok(getLinksAndRespond(s.value(), uriInfo, currentLinks)).links(currentLinks.toArray(new Link[0])).build();

            case JPAWorkoutExerciseAdapterResult.Created<T> c ->
                    Response
                            .status(Response.Status.CREATED)
                            .entity(getLinksAndRespond(c.value(), uriInfo, currentLinks))
                            .location(locationLink(c.value(), uriInfo))
                            .links(currentLinks.toArray(new Link[0]))
                            .build();

            case JPAWorkoutExerciseAdapterResult.Updated<T> u ->
                    Response.
                            ok(getLinksAndRespond(u.value(), uriInfo, currentLinks))
                            .links(currentLinks.toArray(new Link[0]))
                            .build();

            case JPAWorkoutExerciseAdapterResult.Deleted<T> d ->
                    d.success() ? Response.noContent().links(getParentLink(uriInfo)).build() : Response.status(400).build();

            case JPAWorkoutExerciseAdapterResult.Paginated<T> p ->{
                currentLinks.add(Link.fromUri(uriInfo.getAbsolutePath()).rel("self").build());
                currentLinks.add(getParentLink(uriInfo));
                yield  Response
                        .ok(new PaginatedResponseDTO<>(
                                getPaginatedLinks(p.values(), uriInfo, currentLinks, p.page(), p.size(), p.totalPageCount()),
                                p.page(),
                                p.size(),
                        p.totalPageCount()
                ))
                        .links(currentLinks.toArray(new Link[0]))
                        .build();
            }

            case JPAWorkoutExerciseAdapterResult.Failure<T> f ->{
                if(f.reason().equals(JPAWorkoutExerciseAdapterResult.FailureReason.UNAUTHORIZED))
                    currentLinks.add(loginLink(uriInfo));
                yield  Response
                        .status(f.reason().getStatus())
                        .entity(f.reason().name())
                        .links(currentLinks.toArray(new Link[0]))
                        .build();
            }
        };
    }

    private Object getLinksAndRespond(Object value, UriInfo uriInfo, List<Link> outLinks) {
        return switch (value){
            case Workout w -> {
                outLinks.addAll(Arrays.asList(getAllLinks(w, uriInfo)));
                yield toResponse(w);
            }
            case Exercise ex -> {
                outLinks.addAll(Arrays.asList(getAllLinks(ex, uriInfo)));
                yield toResponse(ex);
            }
            case ExerciseSet eSet -> {
                outLinks.addAll(Arrays.asList(getAllLinks(eSet, uriInfo)));
                yield toResponse(eSet);
            }
            default -> value;
        };
    }

    //for pagination
    private List<?> getPaginatedLinks(List<?> values, UriInfo uriInfo, List<Link> outLinks, int page, int size, int totalPages) {
        if (values == null || values.isEmpty()) {
            return List.of();
        }

        Object firstElement = values.getFirst();

        return switch (firstElement) {
            case Workout w -> {
                outLinks.addAll(Arrays.asList(getAllLinks(values.toArray(new Workout[0]), uriInfo, page, size, totalPages)));
                yield values.stream()
                        .map(obj -> WorkoutMapper.toResponse((Workout) obj))
                        .toList();
            }
            case Exercise ex -> {
                outLinks.addAll(Arrays.asList(getAllLinks(values.toArray(new Exercise[0]), uriInfo,  page, size, totalPages)));
                yield values.stream()
                        .map(obj -> ExerciseMapper.toResponse((Exercise) obj))
                        .toList();
            }
            case ExerciseSet eSet -> {
                outLinks.addAll(Arrays.asList(getAllLinks(values.toArray(new ExerciseSet[0]), uriInfo, page, size, totalPages)));
                yield values.stream()
                        .map(obj -> toResponse((ExerciseSet) obj))
                        .toList();
            }
            default -> values;
        };
    }

    //for deletion
    public static Link getParentLink(UriInfo uriInfo) {
        String path = uriInfo.getPath();
        String parentPath = path.substring(0, path.lastIndexOf('/'));

        return Link.fromUriBuilder(uriInfo.getBaseUriBuilder().path(parentPath))
                .rel("up")
                .build();
    }

    //for creation
    private URI locationLink(Object value, UriInfo uriInfo) {
        return switch (value){
            case Workout w -> uriInfo.getAbsolutePathBuilder().path(w.getId().toString()).build();
            case Exercise e -> uriInfo.getAbsolutePathBuilder().path(e.getId().toString()).build();
            case ExerciseSet eSet -> uriInfo.getAbsolutePathBuilder().path(eSet.getId().toString()).build();
            default -> null;
        };
    }
}