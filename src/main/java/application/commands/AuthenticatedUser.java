package application.commands;

import domain.valueobject.UserRole;

public record AuthenticatedUser(Long userId, UserRole role) {
}
