package adapter.in.DTOs.RequestDTOs.coach;

import domain.valueobject.Gender;

import java.time.LocalDate;

public record RegisterCoachDTO(String firstName, String lastName, String email, String password, Gender gender,
                               LocalDate bornOn) {
}
