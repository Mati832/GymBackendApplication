package adapter.in.Links;

import adapter.in.controller.ExerciseController;
import adapter.in.controller.UserController;
import adapter.in.controller.WorkoutController;
import domain.model.Exercise;
import domain.model.ExerciseSet;
import domain.model.Workout;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.UriInfo;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import static adapter.in.Links.LinkFactory.*;

public class WorkoutExerciseLinks {
    //workout links
    public static Link[] getAllLinks(Workout workout, UriInfo uriInfo) {
        return new Link[]{
                //get self uri
                getSelfLink(uriInfo),
                //get user link
                getUserLink(uriInfo, workout.getCreatedByUserId()),
                //get all workouts from user
                getWorkoutsLinkInUser(uriInfo, workout.getCreatedByUserId()),
                //get all exercises in workout
                getExercisesLinkInUser(uriInfo, workout.getCreatedByUserId(), workout.getId()),
                //get all workouts
                getWorkoutsLink(uriInfo)
        };
    }

    //exercise links

    public static Link[] getAllLinks(Exercise exercise, UriInfo uriInfo) {
        return exercise.getWorkoutId() == null ? getAllExerciseLinksWithoutWorkout(exercise, uriInfo) : getAllExerciseLinksWithWorkout(exercise, uriInfo);
    }
    private static Link[] getAllExerciseLinksWithoutWorkout(Exercise exercise, UriInfo uriInfo) {
       return new Link[]{
               //get self uri
               getSelfLink(uriInfo),
               //get user link
               getUserLink(uriInfo, exercise.getCreatedByUserId()),
               //get all exercises from user without workout
               getExercisesLinkInUser(uriInfo, exercise.getCreatedByUserId()),
               //get all exerciseSets from exercise without workout
               getExerciseSetsLinkFromExercise(uriInfo, exercise.getCreatedByUserId(), exercise.getId()),
               //get all exercises
               getExercisesLink(uriInfo)
       };
    }

    private static Link[] getAllExerciseLinksWithWorkout(Exercise exercise, UriInfo uriInfo) {
        return new Link[]{
                //get self uri
                getSelfLink(uriInfo),
                //get user link
                getUserLink(uriInfo, exercise.getCreatedByUserId()),
                //get all exercises from user with workout
                getExercisesLinkInUser(uriInfo, exercise.getCreatedByUserId(), exercise.getWorkoutId()),
                //get all exerciseSets from exercise with workout
                getExerciseSetsLinkFromExercise(uriInfo, exercise.getCreatedByUserId(), exercise.getWorkoutId(), exercise.getId()),
                //get workout in exercise
                getWorkoutLinkInUser(uriInfo, exercise.getCreatedByUserId(), exercise.getWorkoutId()),
                //get all workouts in user
                getWorkoutsLinkInUser(uriInfo, exercise.getCreatedByUserId()),
                //get all exercises
                getExercisesLink(uriInfo)
        };
    }

    //exerciseSets links
    public static Link[] getAllLinks(ExerciseSet exerciseSet,  UriInfo uriInfo) {
        String userId = uriInfo.getPathParameters().getFirst("userID");
        String workoutId = uriInfo.getPathParameters().getFirst("workoutID");
        Long userIdLong = Long.parseLong(userId);
        return new Link[]{
                //get self uri
                getSelfLink(uriInfo),
                //get user link
                getUserLink(uriInfo, userIdLong),
                //get all exerciseSets in user
                workoutId == null ?
                        getExerciseSetsLinkFromExercise(uriInfo, userIdLong, exerciseSet.getBelongsToExercise()) :
                        getExerciseSetsLinkFromExercise(uriInfo, userIdLong, Long.parseLong(workoutId), exerciseSet.getBelongsToExercise()),
                //get exercise link
                workoutId == null ? getExercisesLinkInUser(uriInfo, userIdLong) : getExercisesLinkInUser(uriInfo, userIdLong, Long.parseLong(workoutId)),


        };
    }

    //pagination

    public static Link[] getAllLinks(Workout[] workouts, UriInfo uriInfo, int page, int size, int totalPages) {
       return Stream.concat(
               Arrays.stream(workouts).map(w -> getItemUriLink(w, uriInfo)),
               Stream.of(getPrev(uriInfo, page, size),  getNext(uriInfo, page, size, totalPages))
       ).filter(Objects::nonNull).toArray(Link[]::new);
    }

    public static Link[] getAllLinks(Exercise[] exercises, UriInfo uriInfo, int page, int size, int totalPages) {
        return Stream.concat(
                Arrays.stream(exercises).map(e -> getItemUriLink(e, uriInfo)),
                Stream.of(getPrev(uriInfo, page, size),  getNext(uriInfo, page, size, totalPages))
        ).filter(Objects::nonNull).toArray(Link[]::new);
    }
    public static Link[] getAllLinks(ExerciseSet[] exerciseSets, UriInfo uriInfo,  int page, int size, int totalPages) {
        return Stream.concat(
                Arrays.stream(exerciseSets).map(eSet -> getItemUriLink(eSet, uriInfo)),
                Stream.of(getPrev(uriInfo, page, size),  getNext(uriInfo, page, size, totalPages))
        ).filter(Objects::nonNull).toArray(Link[]::new);
    }


    //item URI's

    //workout item
    private static Link getItemUriLink(Workout workout, UriInfo uriInfo) {
        return Link.fromUriBuilder(uriInfo.getBaseUriBuilder()
                .path(UserController.class)
                .path(workout.getCreatedByUserId().toString())
                .path(WorkoutController.class)
                .path(workout.getId().toString()))
                .rel("item")
                .build();
    }

    //exercise item
    private static Link getItemUriLink(Exercise exercise, UriInfo uriInfo) {
        return exercise.getWorkoutId() == null ? getItemUriExerciseWithoutWorkout(exercise, uriInfo) : getItemUriExerciseWithWorkout(exercise, uriInfo);
    }
    private static Link getItemUriExerciseWithoutWorkout(Exercise exercise, UriInfo uriInfo) {
        return Link.fromUriBuilder(uriInfo.getBaseUriBuilder()
                .path(UserController.class)
                .path(exercise.getCreatedByUserId().toString())
                .path(ExerciseController.class)
                .path(exercise.getId().toString()))
                .rel("item")
                .build();
    }

    private static Link getItemUriExerciseWithWorkout(Exercise exercise, UriInfo uriInfo) {
        return Link.fromUriBuilder(uriInfo.getBaseUriBuilder()
                .path(UserController.class)
                .path(exercise.getCreatedByUserId().toString())
                .path(WorkoutController.class)
                .path(exercise.getWorkoutId().toString())
                .path(ExerciseController.class)
                .path(exercise.getId().toString()))
                .rel("item")
                .build();
    }

    //exerciseSet item
    private static Link getItemUriLink(ExerciseSet exerciseSet, UriInfo uriInfo) {
        return Link.fromUriBuilder(uriInfo.getAbsolutePathBuilder()
                .path(exerciseSet.getId().toString())).rel("item").build();
    }
}
