package adapter.out;

import adapter.out.Entities.ExerciseEntity;
import adapter.out.Entities.ExerciseSetEntity;
import adapter.out.Entities.UserEntity;
import adapter.out.Entities.WorkoutEntity;
import domain.Results.JPAWorkoutExerciseAdapterResult;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExerciseWorkoutAdapterUtils {

    //helper methods
    public static void workoutEqualsWithKey(WorkoutEntity expected, WorkoutEntity actual) {
        assertEquals(expected.getId(), actual.getId());
        workoutEquals(expected, actual);
    }
    public static void workoutEquals(WorkoutEntity expected,  WorkoutEntity actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
        List<ExerciseEntity> expectedExercises = expected.getExercises().stream().sorted(Comparator.comparing(ExerciseEntity::getName)).toList();
        List<ExerciseEntity> actualExercises = actual.getExercises().stream().sorted(Comparator.comparing(ExerciseEntity::getName)).toList();
        assertEquals(expectedExercises.size(), actualExercises.size());

        for(int i = 0; i < expectedExercises.size(); i++) {
            exerciseEquals(expectedExercises.get(i), actualExercises.get(i));
        }
        userEquals(expected.getOwner(), actual.getOwner());
    }


    public static void exerciseEqualsWithKey(ExerciseEntity expected,  ExerciseEntity actual) {
        assertEquals(expected.getId(), actual.getId());
        exerciseEquals(expected, actual);
    }
    public static void exerciseEquals(ExerciseEntity expected,  ExerciseEntity actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getType(), actual.getType());
        assertEquals(expected.getDurationInSec(), actual.getDurationInSec());

        List<ExerciseSetEntity> expectedExerciseSets = expected.getExerciseSets().stream().sorted(Comparator.comparing(ExerciseSetEntity::getCreatedAt)).toList();
        List<ExerciseSetEntity> actualExerciseSets = actual.getExerciseSets().stream().sorted(Comparator.comparing(ExerciseSetEntity::getCreatedAt)).toList();
        assertEquals(expectedExerciseSets.size(), actualExerciseSets.size());

        for(int i = 0; i < expectedExerciseSets.size(); i++) {
            exerciseSetEquals(expectedExerciseSets.get(i), actualExerciseSets.get(i));
        }

        userEquals(expected.getOwner(), actual.getOwner());
        if(! (expected.getWorkout() == null || actual.getWorkout() == null))
            basicWorkoutEquals(expected.getWorkout(), actual.getWorkout());
    }
    private static void basicWorkoutEquals(WorkoutEntity expected, WorkoutEntity actual){
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
    }


    public static void exerciseSetEqualsWithKey(ExerciseSetEntity expected, ExerciseSetEntity actual) {
        assertEquals(expected.getId(), actual.getId());
        exerciseSetEquals(expected, actual);
    }

    public static void exerciseSetEquals(ExerciseSetEntity expected,  ExerciseSetEntity actual) {
        assertEquals(expected.getReps(), actual.getReps());
        assertEquals(expected.getWeightInKg(), actual.getWeightInKg());
        assertEquals(expected.getNotes(), actual.getNotes());
        assertEquals(expected.getDurationInSec(), actual.getDurationInSec());
        assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
    }

    public static void userEquals(UserEntity expected, UserEntity actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getPassword(), actual.getPassword());
        assertEquals(expected.getGender(), actual.getGender());
        assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
    }

    public static <T> void assertResultEquals(
            JPAWorkoutExerciseAdapterResult<T> expected,
            JPAWorkoutExerciseAdapterResult<T> actual,
            Function<T, Object> comparator
    ) {
        switch (expected) {
            case JPAWorkoutExerciseAdapterResult.Success<T>(
                    T expectedSuccess
            ) when actual instanceof JPAWorkoutExerciseAdapterResult.Success<T>(T actualSuccess) ->
                    assertEquals(comparator.apply(expectedSuccess), comparator.apply(actualSuccess));

            case JPAWorkoutExerciseAdapterResult.Created<T>(
                    T expectedCreated
            ) when actual instanceof JPAWorkoutExerciseAdapterResult.Created<T>(T actualCreated) ->
                    assertEquals(comparator.apply(expectedCreated), comparator.apply(actualCreated));

            case JPAWorkoutExerciseAdapterResult.Updated<T>(
                    T expectedUpdated
            ) when actual instanceof JPAWorkoutExerciseAdapterResult.Updated<T>(T actualUpdated) ->
                assertEquals(comparator.apply(expectedUpdated), comparator.apply(actualUpdated));

            case JPAWorkoutExerciseAdapterResult.Deleted<T>(
                    boolean expectedDeleted
            ) when actual instanceof JPAWorkoutExerciseAdapterResult.Deleted<T>(boolean actualDeleted) ->
                assertEquals(expectedDeleted, actualDeleted);

            case JPAWorkoutExerciseAdapterResult.Failure<T>(
                    JPAWorkoutExerciseAdapterResult.FailureReason expectedReason
            ) when actual instanceof JPAWorkoutExerciseAdapterResult.Failure<T>(
                    JPAWorkoutExerciseAdapterResult.FailureReason actualReason
            ) -> assertEquals(expectedReason, actualReason);

            case null, default -> throw new AssertionError("Results do not match because they are of different type");
        }
    }
}
