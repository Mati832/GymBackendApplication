package application.commands;

public record AssignCoachMemberRelationCommand(String coachEmail, String memberEmail, Long requestedBy) {

}
