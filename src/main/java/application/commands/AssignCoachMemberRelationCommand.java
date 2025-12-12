package application.commands;

import application.port.in.AssignCoachMemberRelationUseCase;

import java.time.LocalDate;

public record AssignCoachMemberRelationCommand(String coachEmail, String memberEmail) {

}
