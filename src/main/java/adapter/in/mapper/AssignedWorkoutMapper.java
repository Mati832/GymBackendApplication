package adapter.in.mapper;

import adapter.in.DTOs.ResponseDTOs.member.AssignedWorkoutResponse;
import adapter.in.Links.LinkFactory;
import adapter.in.controller.CoachWebController;
import adapter.in.controller.MemberWebController;
import domain.model.AssignedWorkout;
import domain.valueobject.UserRole;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

@ApplicationScoped
public class AssignedWorkoutMapper {
    public static AssignedWorkoutResponse toResponse(AssignedWorkout assignedWorkout, UriInfo uriInfo) {


        URI self = uriInfo.getBaseUriBuilder().path(MemberWebController.class).path("/"+assignedWorkout.getMemberId()).path("/assigned-workouts/"+assignedWorkout.getId()).build();

        URI member = LinkFactory.getUserLink(uriInfo, assignedWorkout.getMemberId(), UserRole.MEMBER).getUri();

        URI coach = LinkFactory.getUserLink(uriInfo, assignedWorkout.getCoachId(), UserRole.COACH).getUri();

        URI workout = LinkFactory.getWorkoutlLink(uriInfo, assignedWorkout.getWorkoutId()).getUri();

        return new AssignedWorkoutResponse(self, member, coach, workout, assignedWorkout.getAssignedAt());
    }
}
