package application.port.out.AssignedWorkoutPorts;

import domain.model.AssignedWorkout;

import java.util.List;

public interface GetAssignedWorkoutsPort {
    List<AssignedWorkout> getAssignedWorkouts(Long memberId, Long coachId);
}
