package adapter.in.mapper;

import adapter.in.DTOs.ResponseDTOs.member.AssignedWorkoutResponse;
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
                .path("todo")
                .build(assignedWorkout.getId());

        URI member = uriInfo.getBaseUriBuilder()
                .path("todo")
                .path(MemberWebController.class)
                .path(assignedWorkout.getMemberId().toString()).build();
        //eigtl so.build(assignedWorkout.getMemberId());
        URI coach = uriInfo.getBaseUriBuilder()
                .path("todo")
                .path(CoachWebController.class)
                .path(assignedWorkout.getCoachId().toString()).build();
        //eigtl so.build(assignedWorkout.getCoachId());
        URI workout = uriInfo.getBaseUriBuilder()
                .path("todo")
                .build(assignedWorkout.getWorkoutId());

        return new AssignedWorkoutResponse(self, member, coach, workout, assignedWorkout.getAssignedAt());
    }
}
