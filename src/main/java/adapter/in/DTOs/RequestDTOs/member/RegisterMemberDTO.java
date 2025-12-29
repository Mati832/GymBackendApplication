package adapter.in.DTOs.RequestDTOs.member;

import domain.valueobject.Gender;

import java.time.LocalDate;

public record RegisterMemberDTO(String firstName, String lastName, String email, String password, Gender gender,
                                LocalDate bornOn) {
}
