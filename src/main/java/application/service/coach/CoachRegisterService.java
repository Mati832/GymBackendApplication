package application.service.coach;

import application.commands.coach.CoachRegisterCommand;
import application.port.in.coach.CoachRegistrationUseCase;
import application.port.out.UserPorts.FindUserByEmailPort;
import application.port.out.UserPorts.SaveUserPort;
import domain.Results.RegisterUserResult;
import domain.model.Coach;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;

import static domain.Results.RegisterUserResult.UserRegisterFailureReason.*;

@ApplicationScoped
public class CoachRegisterService implements CoachRegistrationUseCase {
    @Inject
    FindUserByEmailPort findUserByEmailPort;

    @Inject
    SaveUserPort saveUserPort;

    @Override
    public RegisterUserResult registerCoach(CoachRegisterCommand coach) {
        if (coach.firstname().isEmpty() || coach.lastName().isEmpty() || coach.email().isEmpty() || coach.password().isEmpty() || coach.bornOn() == null || coach.gender() == null) {
            return new RegisterUserResult.Failure(FIELD_EMPTY);
        }
        if (coach.password().length() < 6) {
            return new RegisterUserResult.Failure(PASSWORD_TOO_WEAK);
        }
        if (coach.bornOn().isAfter(LocalDate.now())) {
            return new RegisterUserResult.Failure(INVALID_BIRTHDAY);
        }
        if (findUserByEmailPort.findByEmail(coach.email()) != null) {
            return new RegisterUserResult.Failure(USER_ALREADY_EXISTS);
        }
        Coach newCoach = new Coach(coach.firstname(), coach.lastName(), coach.email(), coach.password(), coach.gender(), coach.bornOn());
        return new RegisterUserResult.Success(saveUserPort.save(newCoach));
    }
}
