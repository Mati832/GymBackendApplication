package adapter.in.DTOs.member;

import domain.valueobject.Gender;

import java.time.LocalDate;

public record RegisterMemberDTO(String firstname, String lastName, String email, String password, Gender gender,
                                LocalDate bornOn) {
}
