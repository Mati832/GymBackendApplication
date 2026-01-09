package application.commands;

import domain.valueobject.Gender;

public record UserFilter(String firstName, String lastName, String email, Gender gender, Integer minAge, Integer maxAge) {
}
