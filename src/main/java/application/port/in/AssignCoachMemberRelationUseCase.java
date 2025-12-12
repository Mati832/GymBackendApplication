package application.port.in;

import application.commands.AssignCoachMemberRelationCommand;
import domain.Results.AssignCoachMemberRelationResult;

public interface AssignCoachMemberRelationUseCase {
    AssignCoachMemberRelationResult assign(AssignCoachMemberRelationCommand command);
}
