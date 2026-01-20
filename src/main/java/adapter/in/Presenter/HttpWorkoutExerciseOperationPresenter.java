package adapter.in.Presenter;

import adapter.in.DTOs.ResponseDTOs.PaginatedResponseDTO;
import adapter.in.mapper.ExerciseMapper;
import adapter.in.mapper.WorkoutMapper;
import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.Exercise;
import domain.model.ExerciseSet;
import domain.model.Workout;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static adapter.in.Links.LinkFactory.getParentLink;
import static adapter.in.Links.LinkFactory.loginLink;
import static adapter.in.Links.WorkoutExerciseLinks.*;
import static adapter.in.mapper.ExerciseMapper.toResponse;
import static adapter.in.mapper.WorkoutMapper.toResponse;
import static adapter.in.mapper.ExerciseSetMapper.toResponse;
import static adapter.in.services.CacheExpirationFactory.get10sPrivateNoMustValidateExpiration;

@ApplicationScoped
public class HttpWorkoutExerciseOperationPresenter {

    public HttpWorkoutExerciseOperationPresenter() {}
    public <T> Response toHttp(JPAWorkoutExerciseAdapterResult<T> result, UriInfo uriInfo) {
        List<Link> currentLinks = new ArrayList<>();

        return switch (result) {
            case JPAWorkoutExerciseAdapterResult.Success<T> s ->{
                Object response = getLinksAndRespond(s.value(), uriInfo, currentLinks);
                yield Response
                        .ok(response)
                        .links(currentLinks.toArray(new Link[0]))
                        .tag(generateETag(response))
                        .cacheControl(get10sPrivateNoMustValidateExpiration())
                        .build();
            }

            case JPAWorkoutExerciseAdapterResult.Created<T> c ->{
                Object response = getLinksAndRespond(c.value(), uriInfo, currentLinks);
                yield Response
                        .status(Response.Status.CREATED)
                        .entity(response)
                        .location(locationLink(c.value(), uriInfo))
                        .links(currentLinks.toArray(new Link[0]))
                        .tag(generateETag(response))
                        .cacheControl(get10sPrivateNoMustValidateExpiration())
                        .build();
            }

            case JPAWorkoutExerciseAdapterResult.Updated<T> u ->{
                Object response = getLinksAndRespond(u.value(), uriInfo, currentLinks);
                yield Response.
                        ok(response)
                        .links(currentLinks.toArray(new Link[0]))
                        .tag(generateETag(response))
                        .cacheControl(get10sPrivateNoMustValidateExpiration())
                        .build();
            }

            case JPAWorkoutExerciseAdapterResult.Deleted<T> d ->
                    d.success() ? Response.noContent().links(getParentLink(uriInfo)).build() : Response.status(400).build();

            case JPAWorkoutExerciseAdapterResult.Paginated<T> p ->{
                currentLinks.add(Link.fromUri(uriInfo.getAbsolutePath()).rel("self").build());
                currentLinks.add(getParentLink(uriInfo));

                PaginatedResponseDTO response = new PaginatedResponseDTO(
                        getPaginatedLinks(p.values(), uriInfo, currentLinks, p.page(), p.size(), p.totalPageCount()),
                        p.page(),
                        p.size(),
                        p.totalPageCount()
                );
                yield  Response
                        .ok(response)
                        .links(currentLinks.toArray(new Link[0]))
                        .tag(generateETag(response))
                        .cacheControl(get10sPrivateNoMustValidateExpiration())
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

    //for caching
    public <T> Response evaluateCache(JPAWorkoutExerciseAdapterResult<T> result, Request req){
        Object response;

        switch (result) {
            case JPAWorkoutExerciseAdapterResult.Success<T>(T value) ->
                    response = getLinksAndRespond(value, null, null); //get dto object without any links

            case JPAWorkoutExerciseAdapterResult.Paginated<T> p ->
                    response = new  PaginatedResponseDTO(
                            getPaginatedLinks(p.values(), null, null, -1, -1, -1),  //get dto object without any links
                            p.page(),
                            p.size(),
                            p.totalPageCount()
                    );

            case JPAWorkoutExerciseAdapterResult.Failure<T>(
                    JPAWorkoutExerciseAdapterResult.FailureReason reason
            ) -> {
                return Response
                        .status(reason.getStatus())
                        .entity(reason.name())
                        .build();
            }

            case null, default -> {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
        EntityTag etag = new EntityTag(Integer.toString(response.hashCode()));
        Response.ResponseBuilder builder = req.evaluatePreconditions(etag);
        if(builder != null) return builder.cacheControl(get10sPrivateNoMustValidateExpiration()).build();

        return null;
    }

    private Object getLinksAndRespond(Object value, UriInfo uriInfo, List<Link> outLinks) {
        return switch (value){
            case Workout w -> {
                if(outLinks != null) outLinks.addAll(Arrays.asList(getAllLinks(w, uriInfo)));
                yield toResponse(w);
            }
            case Exercise ex -> {
                if(outLinks != null) outLinks.addAll(Arrays.asList(getAllLinks(ex, uriInfo)));
                yield toResponse(ex);
            }
            case ExerciseSet eSet -> {
                if(outLinks != null) outLinks.addAll(Arrays.asList(getAllLinks(eSet, uriInfo)));
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
                if(outLinks != null) outLinks.addAll(Arrays.asList(getAllLinks(values.toArray(new Workout[0]), uriInfo, page, size, totalPages)));
                yield values.stream()
                        .map(obj -> WorkoutMapper.toResponse((Workout) obj))
                        .toList();
            }
            case Exercise ex -> {
                if(outLinks != null) outLinks.addAll(Arrays.asList(getAllLinks(values.toArray(new Exercise[0]), uriInfo,  page, size, totalPages)));
                yield values.stream()
                        .map(obj -> ExerciseMapper.toResponse((Exercise) obj))
                        .toList();
            }
            case ExerciseSet eSet -> {
                if(outLinks != null) outLinks.addAll(Arrays.asList(getAllLinks(values.toArray(new ExerciseSet[0]), uriInfo, page, size, totalPages)));
                yield values.stream()
                        .map(obj -> toResponse((ExerciseSet) obj))
                        .toList();
            }
            default -> values;
        };
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

    //generate eTag
    private EntityTag generateETag(Object value){
        return new EntityTag(Integer.toString(value.hashCode()));
    }
}