package adapter.in.mapper;

import adapter.in.DTOs.ResponseDTOs.AssignedWorkoutResponse;
import adapter.in.controller.CoachWebController;
import adapter.in.controller.MemberWebController;
import domain.model.AssignedWorkout;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

@ApplicationScoped
public class AssignedWorkoutMapper {
    public static AssignedWorkoutResponse toResponse(AssignedWorkout assignedWorkout, UriInfo uriInfo) {
        URI self = uriInfo.getBaseUriBuilder()
                .path("nochHinzufügen")
                .build(assignedWorkout.getId());

        URI member = uriInfo.getBaseUriBuilder()
                .path(MemberWebController.class)
                //gibtsnochnicht.path(MemberWebController.class, "getMember")
                .path("nochHinzufügen")
                .build(assignedWorkout.getMemberId());
        URI coach = uriInfo.getBaseUriBuilder()
                .path(CoachWebController.class)
                //gibtsnochnicht.path(CoachWebController.class, "getCoach")
                .path("nochHinzufügen")
                .build(assignedWorkout.getCoachId());
        URI workout = uriInfo.getBaseUriBuilder()
                .path("nochHinzufügen")
                .build(assignedWorkout.getWorkoutId());

        return new AssignedWorkoutResponse(self, member, coach, workout, assignedWorkout.getAssignedAt());
    }
}
