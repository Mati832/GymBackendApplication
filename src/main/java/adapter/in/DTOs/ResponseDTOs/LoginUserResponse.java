package adapter.in.DTOs.ResponseDTOs;

import domain.valueobject.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record LoginUserResponse(Long userId, String email,
                                String firstName, String lastName,
                                Gender gender, LocalDate bornOn,
                                LocalDateTime createdAt) {
}
