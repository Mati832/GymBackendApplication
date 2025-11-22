package domain.Results;


import domain.model.User;

public sealed interface RegisterUserResult permits RegisterUserResult.Success, RegisterUserResult.Failure{

    record Success(User user) implements RegisterUserResult {}

    record Failure(FailureReason reason) implements RegisterUserResult {}

    interface FailureReason{

    }
    enum UserFailureReason implements FailureReason{
        USER_ALREADY_EXISTS,
        INVALID_EMAIL,
        PASSWORD_TOO_WEAK,
        FIELD_EMPTY,
        INVALID_BIRTHDAY
    }
    enum CoachFailureReason implements FailureReason{
        INVALID_LICENSE
    }
    enum MemberFailureReason implements FailureReason{

    }

}

