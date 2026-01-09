package application.port.out.AssignedWorkoutPorts;

import domain.model.AssignedWorkout;

public interface CreateAssignedWorkoutPort {
    AssignedWorkout createAssignedWorkout(AssignedWorkout assignedWorkout);
}
