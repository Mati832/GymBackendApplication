package application.port.out.AssignedWorkoutPorts;

import domain.dbResults.PagedResult;
import domain.model.AssignedWorkout;

public interface GetAssignedWorkoutsPort {
    PagedResult<AssignedWorkout> getAssignedWorkouts(Long memberId, Long coachId, String search, int offset, int size);
}
