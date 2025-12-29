package application.port.in.coach;

import application.commands.coach.CoachRegisterCommand;
import domain.Results.RegisterUserResult;

public interface CoachRegistrationUseCase {
    RegisterUserResult registerCoach(CoachRegisterCommand command);
}
