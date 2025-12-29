package domain.Results;

import domain.model.User;

public sealed interface LoginUserResult permits LoginUserResult.Success, LoginUserResult.Failure {

    record  Success(User user) implements LoginUserResult {}
    record  Failure(LoginFailureReason reason) implements LoginUserResult {}


    enum LoginFailureReason{
        USER_NOT_FOUND,
        WRONG_PASSWORD
    }
}
