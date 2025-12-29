package application.commands.coach;

import domain.valueobject.Gender;

import java.time.LocalDate;

public record CoachRegisterCommand(String firstname, String lastName, String email, String password, Gender gender,
                                   LocalDate bornOn) {
}
