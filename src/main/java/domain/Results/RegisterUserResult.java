package domain.Results;


import domain.model.User;

public sealed interface RegisterUserResult permits RegisterUserResult.Success, RegisterUserResult.Failure {

    record Success(User user) implements RegisterUserResult {
    }

    record Failure(RegisterFailureReason reason) implements RegisterUserResult {
    }

    interface RegisterFailureReason {

    }

    enum UserRegisterFailureReason implements RegisterFailureReason {
        USER_ALREADY_EXISTS,
        PASSWORD_TOO_WEAK,
        FIELD_EMPTY,
        INVALID_BIRTHDAY
    }

    enum CoachRegisterFailureReason implements RegisterFailureReason {
        INVALID_LICENSE
    }

    enum MemberRegisterFailureReason implements RegisterFailureReason {

    }

}

