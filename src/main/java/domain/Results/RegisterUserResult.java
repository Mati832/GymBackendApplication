package domain.Results;


import domain.model.User;

public sealed interface RegisterUserResult permits RegisterUserResult.Success, RegisterUserResult.Failure{

    record Success(User user) implements RegisterUserResult {}

    record Failure(RegisterFailureReason reason) implements RegisterUserResult {}

    interface RegisterFailureReason {
        int getStatus();
    }
    enum UserRegisterFailureReason implements RegisterFailureReason {
        USER_ALREADY_EXISTS(409),
        PASSWORD_TOO_WEAK(400),
        FIELD_EMPTY(400),
        INVALID_BIRTHDAY(400);

        private final int status;

        UserRegisterFailureReason(int status) {
            this.status = status;
        }

        @Override
        public int getStatus() {
            return status;
        }
    }
    enum CoachRegisterFailureReason implements RegisterFailureReason {
        INVALID_LICENSE(403);

        private final int status;
        CoachRegisterFailureReason(int status) {
            this.status = status;
        }
        @Override
        public int getStatus(){
            return status;
        }
    }
    enum MemberRegisterFailureReason implements RegisterFailureReason {
        ;
        @Override
        public int getStatus(){
            return 500;
        }
    }

}

