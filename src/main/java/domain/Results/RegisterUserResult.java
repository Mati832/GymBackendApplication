package domain.Results;


import domain.model.User;

public sealed interface RegisterUserResult permits RegisterUserResult.Success, RegisterUserResult.Failure{

    record Success(User user) implements RegisterUserResult {}

    record Failure(FailureReason reason) implements RegisterUserResult {}

    interface FailureReason{
        int getStatus();
    }
    enum UserFailureReason implements FailureReason{
        USER_ALREADY_EXISTS(409),
        PASSWORD_TOO_WEAK(400),
        FIELD_EMPTY(400),
        INVALID_BIRTHDAY(400);

        private final int status;

        UserFailureReason(int status) {
            this.status = status;
        }

        @Override
        public int getStatus() {
            return status;
        }
    }
    enum CoachFailureReason implements FailureReason{
        INVALID_LICENSE(403);

        private final int status;
        CoachFailureReason(int status) {
            this.status = status;
        }
        @Override
        public int getStatus(){
            return status;
        }
    }
    enum MemberFailureReason implements FailureReason{
        ;
        @Override
        public int getStatus(){
            return 500;
        }
    }

}

