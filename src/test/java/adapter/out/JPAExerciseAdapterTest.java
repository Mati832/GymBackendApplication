package adapter.out;

import adapter.out.Entities.*;
import application.port.in.exercise.*;
import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.Exercise;
import domain.model.User;
import domain.model.Workout;
import domain.valueobject.Gender;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static adapter.mapper.JPAExerciseMapper.toDomain;
import static adapter.mapper.JPAUserMapper.toDomain;
import static adapter.mapper.JPAWorkoutMapper.toDomain;
import static adapter.out.ExerciseWorkoutAdapterUtils.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestTransaction
public class JPAExerciseAdapterTest {
    @Inject
    EntityManager em;
    @Inject
    AddExerciseToWorkoutUseCase addExerciseToWorkoutUseCase;
    @Inject
    AddExerciseToUserUseCase addExerciseToUserUseCase;
    @Inject
    DeleteExerciseInUserUseCase deleteExerciseInUserUseCase;
    @Inject
    DeleteExerciseInWorkoutUseCase deleteExerciseInWorkoutUseCase;
    @Inject
    EditExerciseUseCase editExerciseUseCase;

    WorkoutEntity workoutEntity1;
    WorkoutEntity workoutEntity2;
    WorkoutEntity workoutEntity3;
    Exercise exercise1;
    Exercise exercise2;
    UserEntity user;
    LocalDateTime now;

    @BeforeEach
    public void setup(){
        now = LocalDateTime.now();
        workoutEntity1 = new WorkoutEntity(null, "Cardio", "doing cardio", now);
        workoutEntity2 = new WorkoutEntity(null, "MMA", "doing some boxing", now);
        workoutEntity3 = new WorkoutEntity(null, "Push", "doing some bench press", now);
        user = new MemberEntity(null, "firstName", "lastName", "email", "password",
                Gender.MALE, LocalDate.of(1990, 12, 1), LocalDateTime.now());
        workoutEntity1.setOwner(user);
        workoutEntity2.setOwner(user);
        workoutEntity3.setOwner(user);
        exercise1 = new Exercise(null, "Squats", "lower body", 650L, user.getId(), new ArrayList<>(), null);
        exercise2 = new Exercise(null, "Cardio", "Jogging", 1200L, user.getId(), new ArrayList<>(), null);
    }

    //ADD EXERCISE TO WORK OUT

    @Test
    public void testAddExerciseToWorkout(){
        em.persist(user);
        em.persist(workoutEntity1);
        JPAWorkoutExerciseAdapterResult<Workout> actualResult = addExerciseToWorkoutUseCase.addExerciseToWorkout(workoutEntity1.getId(), exercise1);
        //check if exercise is in workout
        assertFalse(workoutEntity1.getExercises().isEmpty());
        ExerciseEntity expected = new ExerciseEntity(workoutEntity1.getExercises().getFirst().getId(), "Squats", "lower body", 650L);
        expected.setOwner(user);
        expected.setWorkout(workoutEntity1);
        exerciseEqualsWithKey(expected, workoutEntity1.getExercises().getFirst());
        //test result
        JPAWorkoutExerciseAdapterResult<Workout> expectedResult = new JPAWorkoutExerciseAdapterResult.Success<>(toDomain(workoutEntity1));
        assertResultEquals(expectedResult, actualResult, Workout::getId);
    }

    @Test
    public void testAddExerciseToWorkout2(){
        em.persist(user);
        em.persist(workoutEntity1);
        em.persist(workoutEntity2);
        JPAWorkoutExerciseAdapterResult<Workout> actualResult1 = addExerciseToWorkoutUseCase.addExerciseToWorkout(workoutEntity1.getId(), exercise1);
        JPAWorkoutExerciseAdapterResult<Workout> actualResult2 = addExerciseToWorkoutUseCase.addExerciseToWorkout(workoutEntity2.getId(), exercise1);
        //check if the same exercise was added in both workouts (should be possible)
        assertFalse(workoutEntity1.getExercises().isEmpty());
        assertFalse(workoutEntity2.getExercises().isEmpty());

        ExerciseEntity expected1 = new ExerciseEntity(workoutEntity1.getExercises().getFirst().getId(), "Squats", "lower body", 650L);
        expected1.setOwner(user);
        expected1.setWorkout(workoutEntity1);

        ExerciseEntity expected2 = new ExerciseEntity(workoutEntity2.getExercises().getFirst().getId(), "Squats", "lower body", 650L);
        expected2.setOwner(user);
        expected2.setWorkout(workoutEntity2);

        exerciseEqualsWithKey(expected1, workoutEntity1.getExercises().getFirst());
        exerciseEqualsWithKey(expected2, workoutEntity2.getExercises().getFirst());
        //the saved exercises are two different entities
        assertThrows(AssertionError.class, () -> exerciseEqualsWithKey(expected1, expected2));
        assertThrows(AssertionError.class, () -> exerciseEquals(expected1, expected2));
        //check results:
        JPAWorkoutExerciseAdapterResult<Workout> expectedResult1 =
                new JPAWorkoutExerciseAdapterResult.Success<>(toDomain(workoutEntity1));
        JPAWorkoutExerciseAdapterResult<Workout> expectedResult2 =
                new  JPAWorkoutExerciseAdapterResult.Success<>(toDomain(workoutEntity2));

        assertResultEquals(expectedResult1, actualResult1, Workout::getId);
        assertResultEquals(expectedResult2, actualResult2, Workout::getId);
        //check if there are two exercises in the DB:
        Long count = em.createQuery("SELECT COUNT(e) FROM ExerciseEntity e", Long.class).getSingleResult();
        assertEquals(2, count);
    }

    @Test
    public void testAddExerciseToWorkout3(){
        em.persist(user);
        em.persist(workoutEntity1);

        //adding the same exercise attributes wise twice in the same workout (should work)
        JPAWorkoutExerciseAdapterResult<Workout> actualResult1 = addExerciseToWorkoutUseCase.addExerciseToWorkout(workoutEntity1.getId(), exercise1);
        JPAWorkoutExerciseAdapterResult<Workout> actualResult2 = addExerciseToWorkoutUseCase.addExerciseToWorkout(workoutEntity1.getId(), exercise1);
        assertFalse(workoutEntity1.getExercises().isEmpty());
        assertEquals(2,  workoutEntity1.getExercises().size());
        //test results:
        JPAWorkoutExerciseAdapterResult<Workout> expectedResult1 = new JPAWorkoutExerciseAdapterResult.Success<>(toDomain(workoutEntity1));
        JPAWorkoutExerciseAdapterResult<Workout> expectedResult2 = new JPAWorkoutExerciseAdapterResult.Success<>(toDomain(workoutEntity1));
        assertResultEquals(expectedResult1, actualResult1, Workout::getId);
        assertResultEquals(expectedResult2, actualResult2, Workout::getId);
        exerciseEquals(workoutEntity1.getExercises().getFirst(), workoutEntity1.getExercises().get(1));
        assertThrows(AssertionError.class, () -> exerciseEqualsWithKey(workoutEntity1.getExercises().getFirst(), workoutEntity1.getExercises().get(1)));
        //check if there are two exercises in the DB:
        Long count = em.createQuery("SELECT COUNT(e) FROM ExerciseEntity e", Long.class).getSingleResult();
        assertEquals(2, count);
    }


    @Test
    //the known exercise (already in the DB) will be copied into a workout. So we end up with two exercises in our DB
    public void testAddKnownExerciseToWorkout(){
        em.persist(user);
        em.persist(workoutEntity1);
        ExerciseEntity knownExercise = new ExerciseEntity(null, "Squats", "legs", 670L);
        knownExercise.setOwner(user);
        em.persist(knownExercise);

        //as long as the key of the known exercise is set, the other attributes don't matter,
        //since they will be fetched from the DB
        exercise1.setId(knownExercise.getId());
        JPAWorkoutExerciseAdapterResult<Workout> actualResult = addExerciseToWorkoutUseCase.addExerciseToWorkout(workoutEntity1.getId(), exercise1);
        //check if exercise is in workout
        assertFalse(workoutEntity1.getExercises().isEmpty());
        ExerciseEntity expected = new ExerciseEntity(workoutEntity1.getExercises().getFirst().getId(), "Squats", "legs", 670L);
        expected.setOwner(user);
        expected.setWorkout(workoutEntity1);
        exerciseEqualsWithKey(expected, workoutEntity1.getExercises().getFirst());
        //known exercise is equal attribute wise, because we added that exercise in workout
        exerciseEquals(knownExercise, workoutEntity1.getExercises().getFirst());
        //but is not the same entity
        assertThrows(AssertionError.class, () -> exerciseEqualsWithKey(knownExercise, workoutEntity1.getExercises().getFirst()));
        //check if there are two exercises in the DB: (the known one and the recently added one)
        Long count = em.createQuery("SELECT COUNT(e) FROM ExerciseEntity e", Long.class).getSingleResult();
        assertEquals(2, count);
    }

    @Test
    public void testAddKnownExerciseWithSetsToWorkout(){
        em.persist(user);
        em.persist(workoutEntity1);
        ExerciseEntity knownExercise = new ExerciseEntity(null, "Squats", "legs", 670L);
        knownExercise.setOwner(user);
        em.persist(knownExercise);
        ExerciseSetEntity knownSet1 = new ExerciseSetEntity(null, 10, 30, "none", 120L, now);
        knownSet1.setExercise(knownExercise);
        ExerciseSetEntity knownSet2 = new ExerciseSetEntity(null, 15, 20, "none", 130L, LocalDateTime.now());
        knownSet2.setExercise(knownExercise);
        em.persist(knownSet1);
        em.persist(knownSet2);
        knownExercise.getExerciseSets().add(knownSet1);
        knownExercise.getExerciseSets().add(knownSet2);

        exercise1.setId(knownExercise.getId());
        JPAWorkoutExerciseAdapterResult<Workout> actualResult = addExerciseToWorkoutUseCase.addExerciseToWorkout(workoutEntity1.getId(), exercise1);
        //check the exercise in workout after adding it
        assertFalse(workoutEntity1.getExercises().isEmpty());
        ExerciseEntity expected = new ExerciseEntity(workoutEntity1.getExercises().getFirst().getId(), "Squats", "legs", 670L);
        expected.setOwner(user);
        expected.setWorkout(workoutEntity1);
        expected.getExerciseSets().add(knownSet1);
        expected.getExerciseSets().add(knownSet2);
        exerciseEqualsWithKey(expected, workoutEntity1.getExercises().getFirst());
        //check results:
        JPAWorkoutExerciseAdapterResult<Workout> expectedResult =
                new  JPAWorkoutExerciseAdapterResult.Success<>(toDomain(workoutEntity1));
        assertResultEquals(expectedResult, actualResult, Workout::getId);

    }

    @Test
    //with wrong sets
    public void testAddKnownExerciseWithSetsToWorkout2(){
        em.persist(user);
        em.persist(workoutEntity1);
        ExerciseEntity knownExercise = new ExerciseEntity(null, "Squats", "legs", 670L);
        knownExercise.setOwner(user);
        em.persist(knownExercise);
        ExerciseSetEntity knownSet1 = new ExerciseSetEntity(null, 10, 30, "none", 120L, now);
        knownSet1.setExercise(knownExercise);
        ExerciseSetEntity knownSet2 = new ExerciseSetEntity(null, 15, 20, "none", 130L, LocalDateTime.now());
        knownSet2.setExercise(knownExercise);
        ExerciseSetEntity knownSet3 = new ExerciseSetEntity(null, 5, 10, "none", 130L, LocalDateTime.now());
        knownSet3.setExercise(knownExercise);
        em.persist(knownSet1);
        em.persist(knownSet2);
        knownExercise.getExerciseSets().add(knownSet1);
        knownExercise.getExerciseSets().add(knownSet2);

        exercise1.setId(knownExercise.getId());
        JPAWorkoutExerciseAdapterResult<Workout> actualResult = addExerciseToWorkoutUseCase.addExerciseToWorkout(workoutEntity1.getId(), exercise1);
        //check the exercise in workout after adding it
        assertFalse(workoutEntity1.getExercises().isEmpty());
        ExerciseEntity expected = new ExerciseEntity(workoutEntity1.getExercises().getFirst().getId(), "Squats", "legs", 670L);
        expected.setOwner(user);
        expected.setWorkout(workoutEntity1);
        //Wrong sets in expected (knownSet3 should have been knownSet2)
        expected.getExerciseSets().add(knownSet1);
        expected.getExerciseSets().add(knownSet3);
        assertThrows(AssertionError.class, () -> exerciseEqualsWithKey(expected, workoutEntity1.getExercises().getFirst())) ;
        //check results:
        JPAWorkoutExerciseAdapterResult<Workout> expectedResult =
                new  JPAWorkoutExerciseAdapterResult.Success<>(toDomain(workoutEntity1));
        assertResultEquals(expectedResult, actualResult, Workout::getId);
    }

    @Test
    public void testInvalidExerciseToWorkout(){
        em.persist(user);
        em.persist(workoutEntity1);
        //invalid ids
        JPAWorkoutExerciseAdapterResult<Workout> actualResult1 = addExerciseToWorkoutUseCase.addExerciseToWorkout(null, null);
        JPAWorkoutExerciseAdapterResult<Workout> actualResult2 = addExerciseToWorkoutUseCase.addExerciseToWorkout(4513L, exercise1);
        exercise1.setId(958135L);
        JPAWorkoutExerciseAdapterResult<Workout> actualResult3 = addExerciseToWorkoutUseCase.addExerciseToWorkout(workoutEntity1.getId(), exercise1);
        //check if the new exercise to be added is also empty (no exerciseSets)
        exercise2.getExerciseSets().add(89239L);
        JPAWorkoutExerciseAdapterResult<Workout> actualResult4 = addExerciseToWorkoutUseCase.addExerciseToWorkout(workoutEntity1.getId(), exercise2);
        assertTrue(workoutEntity1.getExercises().isEmpty());
        //check results:
        JPAWorkoutExerciseAdapterResult<Workout> expectedResult1 =
                new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);
        JPAWorkoutExerciseAdapterResult<Workout> expectedResult2 =
                new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.WORKOUT_NOT_FOUND);
        JPAWorkoutExerciseAdapterResult<Workout> expectedResult3 =
                new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_NOT_FOUND);
        JPAWorkoutExerciseAdapterResult<Workout> expectedResult4 =
                new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_SET_NOT_FOUND);
        assertResultEquals(expectedResult1, actualResult1, Workout::getId);
        assertResultEquals(expectedResult2, actualResult2, Workout::getId);
        assertResultEquals(expectedResult3, actualResult3, Workout::getId);
        assertResultEquals(expectedResult4, actualResult4, Workout::getId);
        //check if there is no exercise in the DB
        Long count = em.createQuery("SELECT COUNT(e) FROM ExerciseEntity e", Long.class).getSingleResult();
        assertEquals(0, count);
    }

    //ADDING EXERCISE TO USER (works almost the same as adding exercises into workouts)

    @Test
    public void testAddExerciseToUser(){
        em.persist(user);

        JPAWorkoutExerciseAdapterResult<User> actualResult = addExerciseToUserUseCase.addExerciseToUser(user.getId(), exercise1);
        //test if exercise is added in the user
        assertFalse(user.getExercises().isEmpty());
        assertEquals(1,  user.getExercises().size());
        ExerciseEntity expected =  new  ExerciseEntity(user.getExercises().getFirst().getId(), "Squats", "lower body", 650L);
        expected.setOwner(user);
        exerciseEqualsWithKey(expected, user.getExercises().getFirst());
        //test results
        JPAWorkoutExerciseAdapterResult<User> expectedResult = new JPAWorkoutExerciseAdapterResult.Success<>(toDomain(user));
        assertResultEquals(expectedResult, actualResult, User::getId);
        //check if there is one exercise in the db:
        Long count = em.createQuery("SELECT COUNT(e) FROM ExerciseEntity e", Long.class).getSingleResult();
        assertEquals(1, count);
    }

    //DELETE EXERCISE IN WORKOUT

    @Test
    public void testDeleteExerciseInWorkout(){
        em.persist(user);
        em.persist(workoutEntity1);
        //adding exercise to work out
        addExerciseToWorkoutUseCase.addExerciseToWorkout(workoutEntity1.getId(), exercise1);
        //delete exercise from workout
       JPAWorkoutExerciseAdapterResult<Workout> actualResult =
               deleteExerciseInWorkoutUseCase.deleteExerciseInWorkout(workoutEntity1.getId(), workoutEntity1.getExercises().getFirst().getId());

       assertTrue(workoutEntity1.getExercises().isEmpty());
        //test results:
        JPAWorkoutExerciseAdapterResult<Workout> expectedResult = new JPAWorkoutExerciseAdapterResult.Success<>(toDomain(workoutEntity1));
        assertResultEquals(expectedResult, actualResult, Workout::getId);
        //test if here is no exercise in DB:
        Long count = em.createQuery("SELECT COUNT(e) FROM ExerciseEntity e", Long.class).getSingleResult();
        assertEquals(0, count);
    }

    @Test
    public void testDeleteExerciseInWorkout2(){
        em.persist(user);
        em.persist(workoutEntity1);
        //adding multiple exercises to work out
        addExerciseToWorkoutUseCase.addExerciseToWorkout(workoutEntity1.getId(), exercise1);
        addExerciseToWorkoutUseCase.addExerciseToWorkout(workoutEntity1.getId(), exercise2);
        assertEquals(2,  workoutEntity1.getExercises().size());
        //delete both exercises from workout:
        JPAWorkoutExerciseAdapterResult<Workout> actualResult1 =
                deleteExerciseInWorkoutUseCase.deleteExerciseInWorkout(workoutEntity1.getId(), workoutEntity1.getExercises().getFirst().getId());
        assertEquals(1,  workoutEntity1.getExercises().size());

        JPAWorkoutExerciseAdapterResult<Workout> actualResult2 =
                deleteExerciseInWorkoutUseCase.deleteExerciseInWorkout(workoutEntity1.getId(), workoutEntity1.getExercises().getFirst().getId());
        assertTrue(workoutEntity1.getExercises().isEmpty());

        //test results:
        JPAWorkoutExerciseAdapterResult<Workout> expectedResult1 =
                new  JPAWorkoutExerciseAdapterResult.Success<>(toDomain(workoutEntity1));

        JPAWorkoutExerciseAdapterResult<Workout> expectedResult2 =
                new  JPAWorkoutExerciseAdapterResult.Success<>(toDomain(workoutEntity1));

        assertResultEquals(expectedResult1, actualResult1, Workout::getId);
        assertResultEquals(expectedResult2, actualResult2, Workout::getId);
        //test if there is no exercise in DB:
        Long count = em.createQuery("SELECT COUNT(e) FROM ExerciseEntity e", Long.class).getSingleResult();
        assertEquals(0, count);
    }

    @Test
    public void testDeleteInvalidExerciseInWorkout(){
        em.persist(user);
        em.persist(workoutEntity1);
        addExerciseToWorkoutUseCase.addExerciseToWorkout(workoutEntity1.getId(), exercise1);

        JPAWorkoutExerciseAdapterResult<Workout> actualResult1 =
                deleteExerciseInWorkoutUseCase.deleteExerciseInWorkout(null, null);

        JPAWorkoutExerciseAdapterResult<Workout> actualResult2 =
                deleteExerciseInWorkoutUseCase.deleteExerciseInWorkout(78362L, 59873L);

        JPAWorkoutExerciseAdapterResult<Workout> actualResult3 =
                deleteExerciseInWorkoutUseCase.deleteExerciseInWorkout(workoutEntity1.getId(), 562875L);
        //test results:
        JPAWorkoutExerciseAdapterResult<Workout> expectedResult1 =
                new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);

        JPAWorkoutExerciseAdapterResult<Workout> expectedResult2 =
                new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.WORKOUT_NOT_FOUND);

        JPAWorkoutExerciseAdapterResult<Workout> expectedResult3 =
                new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_IN_WORKOUT_NOT_FOUND);

        assertResultEquals(expectedResult1, actualResult1, Workout::getId);
        assertResultEquals(expectedResult2, actualResult2, Workout::getId);
        assertResultEquals(expectedResult3, actualResult3, Workout::getId);

        //test if there is still the exercise in
        Long count = em.createQuery("SELECT COUNT(e) FROM ExerciseEntity e", Long.class).getSingleResult();
        assertEquals(1, count);
    }


    //DELETE EXERCISE IN USER (works almost the same as deleting an exercise in work out)

    @Test
    public void testDeleteExerciseInUser(){
        em.persist(user);
        addExerciseToUserUseCase.addExerciseToUser(user.getId(), exercise1);
        assertFalse(user.getExercises().isEmpty());
        assertEquals(1,  user.getExercises().size());

        JPAWorkoutExerciseAdapterResult<User> actualResult = deleteExerciseInUserUseCase.deleteExerciseInUser(user.getId(), user.getExercises().getFirst().getId());
        assertTrue(user.getWorkouts().isEmpty());
        //test result
        JPAWorkoutExerciseAdapterResult<User> expectedResult =
                new JPAWorkoutExerciseAdapterResult.Success<>(toDomain(user));
        assertResultEquals(expectedResult, actualResult, User::getId);
        //check if there is no exercise in the DB:
        Long count = em.createQuery("SELECT COUNT(e) FROM ExerciseEntity e", Long.class).getSingleResult();
        assertEquals(0, count);
    }

    @Test
    public void testDeleteInvalidExerciseInUser(){
        em.persist(user);
        addExerciseToUserUseCase.addExerciseToUser(user.getId(), exercise1);
        assertFalse(user.getExercises().isEmpty());
        assertEquals(1,  user.getExercises().size());

        JPAWorkoutExerciseAdapterResult<User> actualResult1 = deleteExerciseInUserUseCase.deleteExerciseInUser(4892L, user.getExercises().getFirst().getId());
        JPAWorkoutExerciseAdapterResult<User> actualResult2 = deleteExerciseInUserUseCase.deleteExerciseInUser(user.getId(), 957982L);

        //test results:
        JPAWorkoutExerciseAdapterResult<User> expectedResult1 =
                new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.USER_NOT_FOUND);
        JPAWorkoutExerciseAdapterResult<User> expectedResult2 =
                new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_IN_USER_NOT_FOUND);
        assertResultEquals(expectedResult1, actualResult1, User::getId);
        assertResultEquals(expectedResult2, actualResult2, User::getId);
        //test if there is 1 exercise in the DB:
        Long count = em.createQuery("SELECT COUNT(e) FROM ExerciseEntity e", Long.class).getSingleResult();
        assertEquals(1, count);
    }

    //EDIT EXERCISE (works both for exercises in the workout and user)

    @Test
    public void testEditExercise(){
        em.persist(user);
        em.persist(workoutEntity1);
        addExerciseToWorkoutUseCase.addExerciseToWorkout(workoutEntity1.getId(), exercise1);

        //change the attributes in exercise:
        Exercise attributesToBeChanged = new Exercise(workoutEntity1.getExercises().getFirst().getId(),
                "Jogging", "doing cardio", 1200L, user.getId(), new ArrayList<>(), null);
       JPAWorkoutExerciseAdapterResult<Exercise> actualResult =
               editExerciseUseCase.editExercise(workoutEntity1.getExercises().getFirst().getId(), attributesToBeChanged);
        //test if exercise was edited
        ExerciseEntity expected = new ExerciseEntity(workoutEntity1.getExercises().getFirst().getId(), "Jogging", "doing cardio", 1200L);
        expected.setOwner(user);
        expected.setWorkout(workoutEntity1);
        exerciseEqualsWithKey(expected, workoutEntity1.getExercises().getFirst());
        //test results
        JPAWorkoutExerciseAdapterResult<Exercise> expectedResult =
                new JPAWorkoutExerciseAdapterResult.Success<>(toDomain(expected));
        assertResultEquals(expectedResult, actualResult, Exercise::getId);
    }

    @Test
    //null value are not written and old values stay the same
    public void testEditExercise2(){
        em.persist(user);
        em.persist(workoutEntity1);
        addExerciseToWorkoutUseCase.addExerciseToWorkout(workoutEntity1.getId(), exercise1);

        //change the attributes in exercise:
        Exercise attributesToBeChanged = new Exercise(workoutEntity1.getExercises().getFirst().getId(),
                null, "doing cardio", 1200L, user.getId(), new ArrayList<>(), null);
        JPAWorkoutExerciseAdapterResult<Exercise> actualResult =
                editExerciseUseCase.editExercise(workoutEntity1.getExercises().getFirst().getId(), attributesToBeChanged);
        //test if exercise was edited
        ExerciseEntity expected = new ExerciseEntity(workoutEntity1.getExercises().getFirst().getId(), "Squats", "doing cardio", 1200L);
        expected.setOwner(user);
        expected.setWorkout(workoutEntity1);
        exerciseEqualsWithKey(expected, workoutEntity1.getExercises().getFirst());
        //test results
        JPAWorkoutExerciseAdapterResult<Exercise> expectedResult =
                new JPAWorkoutExerciseAdapterResult.Success<>(toDomain(expected));
        assertResultEquals(expectedResult, actualResult, Exercise::getId);
    }

    @Test
    public void testEditExerciseInUser(){
        em.persist(user);
        addExerciseToUserUseCase.addExerciseToUser(user.getId(), exercise1);

        Exercise attributesToBeChanged = new Exercise(user.getExercises().getFirst().getId(),
                "jogging", "doing cardio", 1200L, user.getId(), new ArrayList<>(), null);

        JPAWorkoutExerciseAdapterResult<Exercise> actualResult =
                editExerciseUseCase.editExercise(user.getExercises().getFirst().getId(), attributesToBeChanged);
        //test if exercise was edited
        ExerciseEntity expected = new ExerciseEntity(user.getExercises().getFirst().getId(), "jogging", "doing cardio", 1200L);
        expected.setOwner(user);
        expected.setWorkout(workoutEntity1);
        exerciseEqualsWithKey(expected, user.getExercises().getFirst());
        //test results
        JPAWorkoutExerciseAdapterResult<Exercise> expectedResult =
                new JPAWorkoutExerciseAdapterResult.Success<>(toDomain(expected));
        assertResultEquals(expectedResult, actualResult, Exercise::getId);
    }

    @Test
    public void testEditInvalidExercise(){
        em.persist(user);
        addExerciseToUserUseCase.addExerciseToUser(user.getId(), exercise1);

        Exercise attributesToBeChanged = new Exercise(user.getExercises().getFirst().getId(),
                "jogging", "doing cardio", 1200L, user.getId(), new ArrayList<>(), null);
        //wrong ids
        //test result:
        attributesToBeChanged.setId(39761L);
        JPAWorkoutExerciseAdapterResult<Exercise> actualResult1 =
                editExerciseUseCase.editExercise(39761L, attributesToBeChanged);

        JPAWorkoutExerciseAdapterResult<Exercise> actualResult2 =
                editExerciseUseCase.editExercise(user.getExercises().getFirst().getId(), attributesToBeChanged);

        attributesToBeChanged.setCreatedByUserId(482984L);
        attributesToBeChanged.setId(user.getExercises().getFirst().getId());
        JPAWorkoutExerciseAdapterResult<Exercise> actualResult3 = editExerciseUseCase.editExercise(user.getExercises().getFirst().getId(), attributesToBeChanged);


        JPAWorkoutExerciseAdapterResult<Exercise> expectedResult1 =
                new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_NOT_FOUND);

        JPAWorkoutExerciseAdapterResult<Exercise> expectedResult2 =
                new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);

        JPAWorkoutExerciseAdapterResult<Exercise> expectedResult3 =
                new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);

        assertResultEquals(expectedResult1, actualResult1, Exercise::getId);
        assertResultEquals(expectedResult2, actualResult2, Exercise::getId);
        assertResultEquals(expectedResult3, actualResult3, Exercise::getId);

        //test if the exercise has changed: (should not)
        ExerciseEntity expected = new ExerciseEntity(user.getExercises().getFirst().getId(), "Squats", "lower body", 650L);
        expected.setOwner(user);
        exerciseEqualsWithKey(expected, user.getExercises().getFirst());
    }
}