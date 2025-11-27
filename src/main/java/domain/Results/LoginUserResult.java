package domain.Results;

import domain.model.User;

public sealed interface LoginUserResult permits LoginUserResult.Success, LoginUserResult.Failure {

    record  Success(User user) implements LoginUserResult {}
    record  Failure(FailureReason reason) implements LoginUserResult {}

    interface FailureReason{}
    enum UserFailureReason implements FailureReason{
        USER_NOT_FOUND,
        WRONG_PASSWORD
    }
}
