package domain.Results;


import domain.model.User;

public sealed interface RegisterUserResult permits RegisterUserResult.Success, RegisterUserResult.Failure {

    record Success(User user) implements RegisterUserResult {}

    record Failure(FailureReason reason) implements RegisterUserResult {}

    enum FailureReason {
        USER_ALREADY_EXISTS,
        INVALID_EMAIL,
        PASSWORD_TOO_WEAK,
        FIELD_EMPTY,
        INVALID_BIRTHDAY
    }
}

