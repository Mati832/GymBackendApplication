package adapter.in.Links;

import adapter.in.controller.*;
import domain.valueobject.UserRole;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

public class LinkFactory {


    public static Link self(URI uri) {
        return Link.fromUri(uri).rel("self").build();
    }

    public static Link getSelfLink(UriInfo uriInfo, Long id) {
        String path = uriInfo.getPath(); // "users/1/workouts/4"
        String parentPath = path.substring(0, path.lastIndexOf('/')); // "users/1/workouts"

        return self(uriInfo.getBaseUriBuilder().path(parentPath).path(id.toString())
                .build());
    }

    public static Link getSelfLink(UriInfo uriInfo) {
        return Link.fromUri(uriInfo.getAbsolutePath()).rel("self").build();
    }


    //dispatcher link
    public static Link dispatcherLink(UriInfo uriInfo) {
        return Link.fromUriBuilder(uriInfo.getBaseUriBuilder().path(DispatcherController.class).path(DispatcherController.class,"getDispatcher"))
                .rel("get-dispatcher").build();
    }

    //auth links
    public static Link loginLink(UriInfo uriInfo) {
        return Link.fromUriBuilder(uriInfo.getBaseUriBuilder().path(AuthenticationController.class).path(AuthenticationController.class, "login"))
                .rel("login").build();
    }

    //coachLinks
    public static Link coachRegisterLink(UriInfo uriInfo) {
        return Link.fromUriBuilder(uriInfo.getBaseUriBuilder().path(CoachWebController.class).path(CoachWebController.class,"register"))
                .rel("coach-register").build();
    }
    public static Link coachAssignsMemberLink(UriInfo uriInfo) {
        return Link.fromUriBuilder(uriInfo.getBaseUriBuilder().path(CoachWebController.class).path(CoachWebController.class,"assignMember"))
                .rel("coach-assign-member").build();
    }
    public static Link coachAssignsWorkoutToMemberLink(UriInfo uriInfo, Long coachId, Long memberId, Long workoutId) {
        return Link.fromUriBuilder(uriInfo.getBaseUriBuilder().path(CoachWebController.class).path(CoachWebController.class,"assignWorkout"))
                .rel("coach-assigns-workout-to-member")
                .build(coachId,memberId,workoutId);
    }
    //memberLinks
    public static Link memberRegisterLink(UriInfo uriInfo) {
        return Link.fromUriBuilder(uriInfo.getBaseUriBuilder().path(MemberWebController.class).path(MemberWebController.class,"register"))
                .rel("member-register").build();
    }
    public static Link memberAssignsCoachLink(UriInfo uriInfo) {
        return Link.fromUriBuilder(uriInfo.getBaseUriBuilder().path(MemberWebController.class).path(MemberWebController.class,"assignCoach"))
                .rel("member-assigns-coach").build();
    }
    public static Link memberGetsAssignedWorkoutsLink(UriInfo uriInfo, Long memberId) {
        return Link.fromUriBuilder(uriInfo.getBaseUriBuilder().path(MemberWebController.class).path(MemberWebController.class,"getAssignedWorkouts"))
                .rel("member-gets-assigned-workouts").build(memberId);
    }


    //followingLinks are only placeholders. Links are not implemented yet





    // Listen-Ansichten (Plural)
    public static Link getWorkoutsLink(UriInfo uriInfo) {
        return Link.fromUriBuilder(uriInfo.getBaseUriBuilder().path(WorkoutController.class))
                .rel("get-workouts").build();
    }

    public static Link getWorkoutsLinkInUser(UriInfo uriInfo, Long userId) {
        return Link.fromUriBuilder(uriInfo.getBaseUriBuilder().path(UserController.class).path(userId.toString()).path(WorkoutController.class))
                .rel("get-workouts from user").build();
    }

    public static Link getExercisesLinkInUser(UriInfo uriInfo, Long userId, Long workoutId) {
        return Link.fromUriBuilder(uriInfo
                        .getBaseUriBuilder()
                        .path(UserController.class)
                        .path(userId.toString())
                        .path(WorkoutController.class)
                        .path(workoutId.toString())
                        .path(ExerciseController.class))
                .rel("get-workouts from user").build();
    }

    public static Link getExercisesLinkInUser(UriInfo uriInfo, Long userId) {
        return Link.fromUriBuilder(uriInfo
                .getBaseUriBuilder()
                .path(UserController.class)
                .path(userId.toString())
                .path(ExerciseController.class))
                .rel("get-exercises from user").build();
    }

    public static Link getExercisesLink(UriInfo uriInfo) {
        return Link.fromUriBuilder(uriInfo.getBaseUriBuilder().path(ExerciseController.class))
                .rel("get-exercises").build();
    }

    public static Link getExerciseSetsLinkFromExercise(UriInfo uriInfo, Long userId, Long exerciseId) {
        return Link.fromUriBuilder(uriInfo
                .getBaseUriBuilder()
                .path(UserController.class)
                .path(userId.toString())
                .path(ExerciseController.class)
                .path(exerciseId.toString())
                .path(ExerciseSetController.class))
                .rel("get-exerciseSets in exercise").build();
    }

    public static Link getExerciseSetsLinkFromExercise(UriInfo uriInfo, Long userId, Long workoutId, Long exerciseId) {
        return Link.fromUriBuilder(uriInfo
                        .getBaseUriBuilder()
                        .path(UserController.class)
                        .path(userId.toString())
                        .path(WorkoutController.class)
                        .path(workoutId.toString())
                        .path(ExerciseController.class)
                        .path(exerciseId.toString())
                        .path(ExerciseSetController.class))
                .rel("get-exerciseSets in exercise").build();
    }

    // Einzel-Ansichten (Singular)
    public static Link getWorkoutlLink(UriInfo uriInfo, Long workoutId) {
        return Link.fromUriBuilder(uriInfo.getBaseUriBuilder().path(WorkoutController.class).path(workoutId.toString()))
                .rel("get-workout").build();
    }

    public static Link getExerciseLink(UriInfo uriInfo, Long exerciseId) {
        return Link.fromUriBuilder(uriInfo.getBaseUriBuilder().path(ExerciseController.class).path(exerciseId.toString()))
                .rel("get-exercise").build();
    }

    public static Link getUserLink(UriInfo uriInfo, Long userId, UserRole role) {
        Class<?> controller = (role == UserRole.COACH) ? CoachWebController.class : MemberWebController.class;
        return Link.fromUriBuilder(uriInfo.getBaseUriBuilder().path(controller).path(userId.toString()))
                .rel("get-profile").build();
    }

    public static Link getUserLink(UriInfo uriInfo, Long userId) {
        return Link.fromUriBuilder(uriInfo.getBaseUriBuilder().path(UserController.class).path(userId.toString()))
                .rel("get-profile").build();
    }

    public static Link getWorkoutLinkInUser(UriInfo uriInfo, Long userId, Long workoutId) {
        return Link.fromUriBuilder(uriInfo
                        .getBaseUriBuilder()
                        .path(UserController.class)
                        .path(userId.toString())
                        .path(WorkoutController.class)
                        .path(workoutId.toString()))
                .rel("get-workout in user").build();
    }

// --- RELATIONSHIPS & ASSIGNMENTS (READ) ---

    public static Link getAssignedMembersLink(UriInfo uriInfo, Long coachId) {
        return Link.fromUriBuilder(uriInfo.getBaseUriBuilder().path(CoachWebController.class).path(coachId.toString()).path("members"))
                .rel("get-assigned-members").build();
    }

    public static Link getAssignedCoachesLink(UriInfo uriInfo, Long memberId) {
        return Link.fromUriBuilder(uriInfo.getBaseUriBuilder().path(MemberWebController.class).path(memberId.toString()).path("coaches"))
                .rel("get-assigned-coaches").build();
    }

    public static Link getCoachMemberRelationLink(UriInfo uriInfo, Long coachId, Long memberId) {
        return Link.fromUriBuilder(uriInfo.getBaseUriBuilder().path(CoachWebController.class).path(coachId.toString()).path("members").path(memberId.toString()))
                .rel("get-coach-member-relation").build();
    }

// --- ACTIONS (WRITE/POST) ---

    public static Link createWorkoutLink(UriInfo uriInfo) {
        return Link.fromUriBuilder(uriInfo.getBaseUriBuilder().path(WorkoutController.class))
                .rel("create-workout").build();
    }

    public static Link createExerciseLink(UriInfo uriInfo) {
        return Link.fromUriBuilder(uriInfo.getBaseUriBuilder().path(ExerciseController.class))
                .rel("create-exercise").build();
    }

    public static Link addExerciseToWorkoutLink(UriInfo uriInfo, Long workoutId) {

        return Link.fromUriBuilder(uriInfo.getBaseUriBuilder().path(WorkoutController.class).path(workoutId.toString()).path("exercises"))
                .rel("add-exercise-to-workout").build();
    }

    //pagination links
    public static Link getPrev(UriInfo uriInfo, int page, int size) {
        Link prev = null;
        if(page -1 >= 0) prev = Link.fromUriBuilder(uriInfo
                        .getRequestUriBuilder()
                        .replaceQueryParam("page", page-1)
                        .replaceQueryParam("size", size))
                .rel("prev").build();
        return prev;
    }

    public static Link getNext(UriInfo uriInfo, int page, int size, int totalPages) {
        Link next = null;
        if(page + 1 < totalPages) next = Link.fromUriBuilder(uriInfo
                        .getRequestUriBuilder()
                        .replaceQueryParam("page", page+1)
                        .replaceQueryParam("size", size))
                .rel("next").build();
        return next;
    }
}
