package domain.Results;


import java.util.List;

public sealed interface JPAWorkoutExerciseAdapterResult<T> permits JPAWorkoutExerciseAdapterResult.Failure, JPAWorkoutExerciseAdapterResult.Success,
JPAWorkoutExerciseAdapterResult.Paginated{
    record Success<T>(T value) implements JPAWorkoutExerciseAdapterResult<T> {}
    record Paginated<T>(List<T> values, int page, int size, int totalPageCount) implements JPAWorkoutExerciseAdapterResult<T>{}
    record Failure<T>(FailureReason reason) implements JPAWorkoutExerciseAdapterResult<T> {}
    enum FailureReason {
        USER_NOT_FOUND(404),
        INVALID_REQUEST(400),
        WORKOUT_NOT_FOUND(404),
        WORKOUT_IN_USER_NOT_FOUND(404),
        WORKOUT_NOT_DELETED(400),
        EXERCISE_NOT_DELETED(400),
        EXERCISE_SET_NOT_DELETED(400),
        EXERCISE_IN_WORKOUT_NOT_FOUND(400),
        EXERCISE_SET_IN_EXERCISE_NOT_FOUND(400),
        EXERCISE_IN_USER_NOT_FOUND(400),
        EXERCISE_NOT_FOUND(404),
        EXERCISE_SET_NOT_FOUND(404),
        UNAUTHORIZED(401),
        NO_PERMISSIONS(403);

        int status;
        FailureReason(int status){}
    }
}
