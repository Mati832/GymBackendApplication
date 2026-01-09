package application.port.in.coach;

import application.commands.coach.AssignWorkoutCommand;
import domain.Results.coach.AssignWorkoutResult;

public interface AssignWorkoutUseCase {
    AssignWorkoutResult assign(AssignWorkoutCommand command);
}
