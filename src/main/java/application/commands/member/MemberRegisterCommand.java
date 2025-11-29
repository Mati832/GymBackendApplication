package application.commands.member;

import domain.valueobject.Gender;

import java.time.LocalDate;

public record MemberRegisterCommand(String firstname, String lastName, String email, String password, Gender gender,
                                    LocalDate bornOn) {
}
