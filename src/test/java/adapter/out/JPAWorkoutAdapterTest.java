package adapter.out;

import adapter.out.Entities.*;
import application.port.in.workout.AddWorkoutToUserUseCase;
import application.port.in.workout.DeleteWorkoutInUserUseCase;
import application.port.in.workout.EditWorkoutInUserUseCase;
import domain.Results.JPAWorkoutExerciseAdapterResult;
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

import static adapter.mapper.JPAWorkoutMapper.toDomain;
import static adapter.out.ExerciseWorkoutAdapterUtils.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestTransaction
public class JPAWorkoutAdapterTest {
    @Inject
    AddWorkoutToUserUseCase addWorkoutToUserUseCase;
    @Inject
    DeleteWorkoutInUserUseCase deleteWorkoutInUserUseCase;
    @Inject
    EditWorkoutInUserUseCase editWorkoutInUserUseCase;

    @Inject
    EntityManager em;

    UserEntity user;
    LocalDateTime now;
    Workout workout1;
    Workout workout2;
    Workout workout3;

    @BeforeEach
    public void setup() {
        now = LocalDateTime.now();
        user = new MemberEntity(null, "FirstName", "LastName", "email", "password",
                Gender.MALE, LocalDate.of(1990, 12, 1), LocalDateTime.now());
        workout1 = new Workout(null, "Cardio", "doing cardio", now, new ArrayList<>(), null);
        workout2 = new Workout(null, "Upper body", "Everything related to upper body", now, new ArrayList<>(), null);
        workout3 = new Workout(null, "Lower body", "Everything related to lower body", now, new ArrayList<>(), null);
    }

    //ADDING WORKOUT:

    @Test
    public void testAddWorkout1(){
        em.persist(user);
        workout1.setCreatedByUserId(user.getId());
        JPAWorkoutExerciseAdapterResult<Workout> actualResult = addWorkoutToUserUseCase.addWorkoutToUser(user.getId(), workout1);
        WorkoutEntity expected = new WorkoutEntity(user.getWorkouts().getFirst().getId(), "Cardio", "doing cardio", now);
        expected.setOwner(user);
        //Test if workout is correctly saved in user:
        workoutEquals(expected, user.getWorkouts().getFirst());
        //Test if workout is saved in the DB:
        workoutEquals(expected, em.find(WorkoutEntity.class, user.getWorkouts().getFirst().getId()));
        //Test the result:
        JPAWorkoutExerciseAdapterResult<Workout> expectedResult = new JPAWorkoutExerciseAdapterResult.Created<>(toDomain(user.getWorkouts().getFirst()));
        assertResultEquals(expectedResult, actualResult, Workout::getId);
    }


    @Test
    public void testAddWorkout2(){
        em.persist(user);
        workout1.setCreatedByUserId(user.getId());
        workout2.setCreatedByUserId(user.getId());
        workout3.setCreatedByUserId(user.getId());
        JPAWorkoutExerciseAdapterResult<Workout> actualResult1 = addWorkoutToUserUseCase.addWorkoutToUser(user.getId(), workout1);
        JPAWorkoutExerciseAdapterResult<Workout> actualResult2 = addWorkoutToUserUseCase.addWorkoutToUser(user.getId(), workout2);
        JPAWorkoutExerciseAdapterResult<Workout> actualResult3 = addWorkoutToUserUseCase.addWorkoutToUser(user.getId(), workout3);

        WorkoutEntity expected1 = new WorkoutEntity(user.getWorkouts().getFirst().getId(), "Cardio", "doing cardio", now);
        WorkoutEntity expected2 = new WorkoutEntity(user.getWorkouts().get(1).getId(), "Upper body", "Everything related to upper body", now);
        WorkoutEntity expected3 = new WorkoutEntity(user.getWorkouts().get(2).getId(), "Lower body", "Everything related to lower body", now);
        expected1.setOwner(user);
        expected2.setOwner(user);
        expected3.setOwner(user);
        //Test if workout is correctly saved in user:
        workoutEquals(expected1, user.getWorkouts().getFirst());
        workoutEquals(expected2, user.getWorkouts().get(1));
        workoutEquals(expected3, user.getWorkouts().get(2));
        assertThrows(AssertionError.class, () -> workoutEquals(expected1, user.getWorkouts().get(2)));
        assertThrows(AssertionError.class, () -> workoutEquals(expected3, user.getWorkouts().getFirst()));
        assertThrows(AssertionError.class, () -> workoutEquals(expected2, user.getWorkouts().get(2)));
        assertThrows(AssertionError.class, () -> workoutEquals(expected1, user.getWorkouts().get(1)));
        //Test if workout is saved in DB
        workoutEquals(expected1, em.find(WorkoutEntity.class, user.getWorkouts().getFirst().getId()));
        workoutEquals(expected2, em.find(WorkoutEntity.class, user.getWorkouts().get(1).getId()));
        workoutEquals(expected3, em.find(WorkoutEntity.class, user.getWorkouts().get(2).getId()));
        //Test results:
        //Same expected result since we are operating on the same user
        JPAWorkoutExerciseAdapterResult<Workout> expectedResult1 =  new JPAWorkoutExerciseAdapterResult.Created<>(toDomain(user.getWorkouts().getFirst()));
        JPAWorkoutExerciseAdapterResult<Workout> expectedResult2 =  new JPAWorkoutExerciseAdapterResult.Created<>(toDomain(user.getWorkouts().get(1)));
        JPAWorkoutExerciseAdapterResult<Workout> expectedResult3 =  new JPAWorkoutExerciseAdapterResult.Created<>(toDomain(user.getWorkouts().getLast()));
        assertResultEquals(expectedResult1, actualResult1, Workout::getId);
        assertResultEquals(expectedResult2, actualResult2, Workout::getId);
        assertResultEquals(expectedResult3, actualResult3, Workout::getId);

    }

    @Test
    public void testAddSameWorkout(){
        em.persist(user);
        workout1.setCreatedByUserId(user.getId());
        //should be possible. It is in the users responsibility
        JPAWorkoutExerciseAdapterResult<Workout> actualResult1 = addWorkoutToUserUseCase.addWorkoutToUser(user.getId(), workout1);
        JPAWorkoutExerciseAdapterResult<Workout> actualResult2 = addWorkoutToUserUseCase.addWorkoutToUser(user.getId(), workout1);

        WorkoutEntity expected1 = new WorkoutEntity(user.getWorkouts().getFirst().getId(), "Cardio", "doing cardio", now);
        expected1.setOwner(user);
        WorkoutEntity expected2 = new WorkoutEntity(user.getWorkouts().get(1).getId(), "Cardio", "doing cardio", now);
        expected2.setOwner(user);

        //Test if workout is saved in the user:
        workoutEquals(expected1, user.getWorkouts().getFirst());
        workoutEquals(expected2, user.getWorkouts().get(1));
        assertThrows(AssertionError.class, () -> workoutEqualsWithKey(expected1, user.getWorkouts().get(1)));
        assertThrows(AssertionError.class, () -> workoutEqualsWithKey(expected2, user.getWorkouts().getFirst()));

        //Test if workout is saved in the DB:
        workoutEquals(expected1, em.find(WorkoutEntity.class, user.getWorkouts().getFirst().getId()));
        workoutEquals(expected2, em.find(WorkoutEntity.class, user.getWorkouts().get(1).getId()));
        //Test results:
        JPAWorkoutExerciseAdapterResult<Workout>  expectedResult1 =  new JPAWorkoutExerciseAdapterResult.Created<>(toDomain(user.getWorkouts().getFirst()));
        JPAWorkoutExerciseAdapterResult<Workout>  expectedResult2 =  new JPAWorkoutExerciseAdapterResult.Created<>(toDomain(user.getWorkouts().getLast()));
        assertResultEquals(expectedResult1, actualResult1, Workout::getId);
        assertResultEquals(expectedResult2, actualResult2, Workout::getId);

    }

    @Test
    public void testAddUnknownWorkout(){
        em.persist(user);
        workout1.setCreatedByUserId(user.getId());
        //workout1 is unknown to the DB, because a random key is set and it was never persisted before
        workout1.setId(2031L);
        JPAWorkoutExerciseAdapterResult<Workout> actualResult = addWorkoutToUserUseCase.addWorkoutToUser(user.getId(), workout1);
        //Test if workout is saved in the user:
        assertTrue(user.getWorkouts().isEmpty());
        //Test if workout is saved in the DB:
        assertNull(em.find(WorkoutEntity.class, workout1.getId()));
        //Test result:
        JPAWorkoutExerciseAdapterResult<Workout> expectedResult =
                new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.WORKOUT_NOT_FOUND);
        assertResultEquals(expectedResult, actualResult, Workout::getId);
    }

    @Test
    //A known workout (in DB) is newly copied into the user
    public void testAddKnownWorkout(){
        em.persist(user);
        WorkoutEntity knownWorkout = new WorkoutEntity(null, "Cardio", "doing cardio", now);
        knownWorkout.setOwner(user);
        em.persist(knownWorkout);
        JPAWorkoutExerciseAdapterResult<Workout> actualResult = addWorkoutToUserUseCase.addWorkoutToUser(user.getId(), toDomain(knownWorkout));
        //Test if the workout is saved in the User:
        WorkoutEntity expected = new WorkoutEntity(null, "Cardio", "doing cardio", now);
        expected.setOwner(user);
        assertNotNull(user.getWorkouts().getFirst());
        //Is not equal since they don't have the same PKs
        assertThrows(AssertionError.class, () -> workoutEqualsWithKey(expected, user.getWorkouts().getFirst()));
        //However the attributes should be equal:
        workoutEquals(expected, user.getWorkouts().getFirst());
        //test results:
        JPAWorkoutExerciseAdapterResult<Workout> expectedResult =  new JPAWorkoutExerciseAdapterResult.Created<>(toDomain(user.getWorkouts().getFirst()));
        assertResultEquals(expectedResult, actualResult, Workout::getId);
        //check if both workouts (the known and copied workout in user) are in the DB
        Long count = em.createQuery("SELECT COUNT(w) FROM WorkoutEntity w", Long.class).getSingleResult();
        assertEquals(2, count);
    }

    @Test
    //A new Workout
    public void testAddInvalidWorkoutWithExercise(){
        em.persist(user);
        workout1.setCreatedByUserId(user.getId());
        //Exercise with the id 1235L doesn't exist
        workout1.getExercises().add(1235L);
        JPAWorkoutExerciseAdapterResult<Workout> actualResult = addWorkoutToUserUseCase.addWorkoutToUser(user.getId(), workout1);
        //Test if the invalid workout is even saved in user
        assertTrue(user.getWorkouts().isEmpty());
        //Test result:
        JPAWorkoutExerciseAdapterResult<Workout> expectedResult =
                new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);
        assertResultEquals(expectedResult, actualResult, Workout::getId);
    }
    @Test
    //this should not save any exercise to User since it is a newly created workout with exercises.
    // This is not possible. The user has to create a new empty workout first and then add exercises: see in JPAWorkoutAdapter
    public void testAddInvalidWorkoutWithExercise2(){
        em.persist(user);
        ExerciseEntity exerciseEntity = new ExerciseEntity(null, "Bench press", "push", 600L, now);
        exerciseEntity.setOwner(user);

        //Known exercise to the DB
        em.persist(exerciseEntity);
        workout1.setCreatedByUserId(user.getId());
        workout1.getExercises().add(exerciseEntity.getId());
        JPAWorkoutExerciseAdapterResult<Workout> actualResult = addWorkoutToUserUseCase.addWorkoutToUser(user.getId(), workout1);
        //Test if workout is saved in User
        assertTrue(user.getWorkouts().isEmpty());
        //test results:
        JPAWorkoutExerciseAdapterResult<Workout> expectedResult =
                new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);
        assertResultEquals(expectedResult, actualResult, Workout::getId);
    }


    @Test
    //prerequisites: both workout and exercise are saved in DB
    public void testAddValidWorkoutWithExercise(){
        em.persist(user);

        ExerciseEntity exerciseEntity = new ExerciseEntity(null, "Bench press", "push", 600L, now);
        exerciseEntity.setOwner(user);
        WorkoutEntity workoutEntity = new WorkoutEntity(null, "Cardio",  "doing cardio", now);
        workoutEntity.setOwner(user);
        workoutEntity.getExercises().add(exerciseEntity);
        //Known workout and exercise to the DB
        em.persist(workoutEntity);
        workout1.setCreatedByUserId(user.getId());
        workout1.getExercises().add(exerciseEntity.getId());
        workout1.setId(workoutEntity.getId());

        JPAWorkoutExerciseAdapterResult<Workout> actualResult = addWorkoutToUserUseCase.addWorkoutToUser(user.getId(), workout1);
        //Test if workout is saved in User:
        WorkoutEntity expected = new WorkoutEntity(user.getWorkouts().getFirst().getId(), "Cardio", "doing cardio", now);
        expected.setOwner(user);
        expected.getExercises().add(exerciseEntity);
        workoutEquals(expected, user.getWorkouts().getFirst());
        assertNotEquals(exerciseEntity.getId(), user.getWorkouts().getFirst().getExercises().getFirst().getId());
        //Test results:
        JPAWorkoutExerciseAdapterResult<Workout> expectedResult =
                new JPAWorkoutExerciseAdapterResult.Created<>(toDomain(user.getWorkouts().getFirst()));
        assertResultEquals(expectedResult, actualResult, Workout::getId);
        //check if both workouts (the known and copied workout in user) are in the DB
        Long count = em.createQuery("SELECT COUNT(w) FROM WorkoutEntity w", Long.class).getSingleResult();
        assertEquals(2, count);
    }

    @Test
    public void testAddWorkoutWithWrongUserId(){
        em.persist(user);
        JPAWorkoutExerciseAdapterResult<Workout> actualResult = addWorkoutToUserUseCase.addWorkoutToUser(1231L, workout1);
        //test if workout is saved in user:
        assertTrue(user.getWorkouts().isEmpty());
        //test result:
        JPAWorkoutExerciseAdapterResult<Workout> expectedResult =
                new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.USER_NOT_FOUND);
        assertResultEquals(expectedResult, actualResult, Workout::getId);

    }

    //DELETE WORKOUT

    @Test
    public void testDeleteWorkoutInUser(){
        em.persist(user);
        workout1.setCreatedByUserId(user.getId());
        addWorkoutToUserUseCase.addWorkoutToUser(user.getId(), workout1);
        assertFalse(user.getWorkouts().isEmpty());
        JPAWorkoutExerciseAdapterResult<Void> actualResult = deleteWorkoutInUserUseCase.deleteWorkoutInUser(user.getId(), user.getWorkouts().getFirst().getId());
        //test if workout is deleted in the user:
        assertTrue(user.getWorkouts().isEmpty());
        //test result
        JPAWorkoutExerciseAdapterResult<Void> expectedResult = new JPAWorkoutExerciseAdapterResult.Deleted<>(true);
        assertResultEquals(expectedResult, actualResult, null);
        //check if there is no WorkoutEntity in the DB
        Long count = em.createQuery("SELECT COUNT(w) FROM WorkoutEntity w", Long.class).getSingleResult();
        assertEquals(0, count);
    }

    @Test
    public void testDeleteWorkoutInUser2(){
        em.persist(user);
        workout1.setCreatedByUserId(user.getId());
        workout2.setCreatedByUserId(user.getId());
        addWorkoutToUserUseCase.addWorkoutToUser(user.getId(), workout1);
        addWorkoutToUserUseCase.addWorkoutToUser(user.getId(), workout2);
        assertFalse(user.getWorkouts().isEmpty());
        JPAWorkoutExerciseAdapterResult<Void> actualResult = deleteWorkoutInUserUseCase.deleteWorkoutInUser(user.getId(), user.getWorkouts().getFirst().getId());
        //test if workout is deleted in the user:
        WorkoutEntity wEntityInList = new WorkoutEntity(null, "Upper body", "Everything related to upper body", now);
        wEntityInList.setOwner(user);
        assertFalse(user.getWorkouts().isEmpty());
        assertEquals(1, user.getWorkouts().size());
        workoutEquals(wEntityInList, user.getWorkouts().getFirst());
        //test results
        JPAWorkoutExerciseAdapterResult<Void> expectedResult = new JPAWorkoutExerciseAdapterResult.Deleted<>(true);
        assertResultEquals(expectedResult, actualResult, null);
        //check if there is no WorkoutEntity in the DB
        Long count = em.createQuery("SELECT COUNT(w) FROM WorkoutEntity w", Long.class).getSingleResult();
        assertEquals(1, count);
    }

    @Test
    public void testDeleteWorkoutWithExercises(){
        em.persist(user);

        ExerciseEntity exerciseEntity = new ExerciseEntity(null, "Bench press", "push", 600L, now);
        exerciseEntity.setOwner(user);
        WorkoutEntity workoutEntity = new WorkoutEntity(null, "Cardio",  "doing cardio", now);
        workoutEntity.setOwner(user);
        workoutEntity.getExercises().add(exerciseEntity);
        //Known workout and exercise to the DB
        em.persist(workoutEntity);
        workout1.setCreatedByUserId(user.getId());
        workout1.getExercises().add(exerciseEntity.getId());
        workout1.setId(workoutEntity.getId());
        addWorkoutToUserUseCase.addWorkoutToUser(user.getId(), workout1);
        assertFalse(user.getWorkouts().isEmpty());

       //Test: how many entities are in the DB after saving one additional WorkoutEntity to User
        Long workoutCount = em.createQuery("SELECT COUNT(w) FROM WorkoutEntity w", Long.class).getSingleResult();
        assertEquals(2, workoutCount);
        Long exerciseCount = em.createQuery("SELECT COUNT(e) FROM ExerciseEntity e", Long.class).getSingleResult();
        assertEquals(2, exerciseCount);

        //Delete the workout in user
        JPAWorkoutExerciseAdapterResult<Void> actualResult = deleteWorkoutInUserUseCase.deleteWorkoutInUser(user.getId(), user.getWorkouts().getFirst().getId());
        //Test if workout in user is deleted
        assertTrue(user.getWorkouts().isEmpty());

        //Test if workout AND the exercise are deleted from the DB
        workoutCount = em.createQuery("SELECT COUNT(w) FROM WorkoutEntity w", Long.class).getSingleResult();
        assertEquals(1, workoutCount);
        exerciseCount = em.createQuery("SELECT COUNT(e) FROM ExerciseEntity e", Long.class).getSingleResult();
        assertEquals(1, exerciseCount);
        //Test result
        JPAWorkoutExerciseAdapterResult<Void> expectedResult = new JPAWorkoutExerciseAdapterResult.Deleted<>(true);
        assertResultEquals(expectedResult, actualResult, null);
    }

    @Test
    public void testDeleteWorkoutWithInvalidIds(){
        em.persist(user);
        assertTrue(user.getWorkouts().isEmpty());
        //test results
        JPAWorkoutExerciseAdapterResult<Void> actualResult1 = deleteWorkoutInUserUseCase.deleteWorkoutInUser(user.getId(), user.getId());
        JPAWorkoutExerciseAdapterResult<Void>  expectedResult1 =
                new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.WORKOUT_NOT_FOUND);
        assertResultEquals(expectedResult1, actualResult1, null);

        JPAWorkoutExerciseAdapterResult<Void> actualResult2 = deleteWorkoutInUserUseCase.deleteWorkoutInUser(10L, 10L);
        JPAWorkoutExerciseAdapterResult<Void> expectedResult2 =
                new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.USER_NOT_FOUND);
        assertResultEquals(expectedResult2, actualResult2, null);

        workout1.setCreatedByUserId(user.getId());
        addWorkoutToUserUseCase.addWorkoutToUser(user.getId(), workout1);
        assertFalse(user.getWorkouts().isEmpty());
        JPAWorkoutExerciseAdapterResult<Void> actualResult3 = deleteWorkoutInUserUseCase.deleteWorkoutInUser(user.getId(), 100L);
        JPAWorkoutExerciseAdapterResult<Void> expectedResult3 =
                new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.WORKOUT_NOT_FOUND);
        assertResultEquals(expectedResult3, actualResult3, null);
        assertFalse(user.getWorkouts().isEmpty());
    }

    //EDIT WORKOUT

    @Test
    public void testEditWorkoutInUser(){
        em.persist(user);
        workout1.setCreatedByUserId(user.getId());
        addWorkoutToUserUseCase.addWorkoutToUser(user.getId(), workout1);
        assertFalse(user.getWorkouts().isEmpty());

        Workout editedWorkout = new Workout(null, "strength training", "training for strength", now, new ArrayList<>(), user.getId());
        editedWorkout.setId(user.getWorkouts().getFirst().getId());
        JPAWorkoutExerciseAdapterResult<Workout> actualResult = editWorkoutInUserUseCase.editWorkoutInUser(user.getWorkouts().getFirst().getId(), editedWorkout);

        WorkoutEntity expectedAfterEditing = new WorkoutEntity(user.getWorkouts().getFirst().getId(),  "strength training", "training for strength", now);
        expectedAfterEditing.setId(editedWorkout.getId());
        expectedAfterEditing.setOwner(user);
        //test if the workout has been edited in the user
        workoutEquals(expectedAfterEditing, user.getWorkouts().getFirst());
        //test results
        JPAWorkoutExerciseAdapterResult<Workout> expectedResult = new JPAWorkoutExerciseAdapterResult.Updated<>(toDomain(user.getWorkouts().getFirst()));
        assertResultEquals(expectedResult, actualResult, Workout::getId);
    }

    @Test
    public void testEditWorkoutInUser2(){
        em.persist(user);
        workout1.setCreatedByUserId(user.getId());
        addWorkoutToUserUseCase.addWorkoutToUser(user.getId(), workout1);
        assertFalse(user.getWorkouts().isEmpty());

        Workout editedWorkout = new Workout(null, null, "training for strength", now, new ArrayList<>(), user.getId());
        editedWorkout.setId(user.getWorkouts().getFirst().getId());
        JPAWorkoutExerciseAdapterResult<Workout> actualResult = editWorkoutInUserUseCase.editWorkoutInUser(user.getWorkouts().getFirst().getId(), editedWorkout);

        WorkoutEntity expectedAfterEditing = new WorkoutEntity(user.getWorkouts().getFirst().getId(),  "Cardio", "training for strength", now);
        expectedAfterEditing.setId(editedWorkout.getId());
        expectedAfterEditing.setOwner(user);
        //test if the workout has been edited in the user
        workoutEquals(expectedAfterEditing, user.getWorkouts().getFirst());
        //test results
        JPAWorkoutExerciseAdapterResult<Workout> expectedResult = new JPAWorkoutExerciseAdapterResult.Updated<>(toDomain(user.getWorkouts().getFirst()));
        assertResultEquals(expectedResult, actualResult, Workout::getId);
    }

    @Test
    public void testEditWorkoutInUser3(){
        em.persist(user);
        workout1.setCreatedByUserId(user.getId());
        workout2.setCreatedByUserId(user.getId());
        addWorkoutToUserUseCase.addWorkoutToUser(user.getId(), workout1);
        addWorkoutToUserUseCase.addWorkoutToUser(user.getId(), workout2);
        assertFalse(user.getWorkouts().isEmpty());

        Workout editedWorkout1 = new Workout(null, "strength training", "training for strength", now, new ArrayList<>(), user.getId());
        editedWorkout1.setId(user.getWorkouts().getFirst().getId());
        Workout editedWorkout2 = new Workout(null, "MMA", "doing some Boxing", now, new ArrayList<>(), user.getId());
        editedWorkout2.setId(user.getWorkouts().get(1).getId());
        JPAWorkoutExerciseAdapterResult<Workout> actualResult1 = editWorkoutInUserUseCase.editWorkoutInUser(user.getWorkouts().getFirst().getId(), editedWorkout1);
        JPAWorkoutExerciseAdapterResult<Workout> actualResult2 = editWorkoutInUserUseCase.editWorkoutInUser(user.getWorkouts().get(1).getId(), editedWorkout2);

        WorkoutEntity expected1 = new WorkoutEntity(user.getWorkouts().getFirst().getId(),  "strength training", "training for strength", now);
        expected1.setId(editedWorkout1.getId());
        expected1.setOwner(user);
        WorkoutEntity expected2 = new WorkoutEntity(user.getWorkouts().get(1).getId(),  "MMA", "doing some Boxing", now);
        expected2.setId(editedWorkout2.getId());
        expected2.setOwner(user);
        //test if the workout has been edited in the user
        workoutEquals(expected1, user.getWorkouts().getFirst());
        workoutEquals(expected2, user.getWorkouts().get(1));
        assertFalse(user.getWorkouts().isEmpty());
        //test results
        JPAWorkoutExerciseAdapterResult<Workout> expectedResult1 = new JPAWorkoutExerciseAdapterResult.Updated<>(toDomain(user.getWorkouts().getFirst()));
        JPAWorkoutExerciseAdapterResult<Workout> expectedResult2 = new JPAWorkoutExerciseAdapterResult.Updated<>(toDomain(user.getWorkouts().get(1)));
        assertResultEquals(expectedResult1, actualResult1, Workout::getId);
        assertResultEquals(expectedResult2, actualResult2, Workout::getId);

    }

    @Test
    public void testInvalidEditWorkoutInUser(){
        em.persist(user);
        workout1.setCreatedByUserId(user.getId());
        addWorkoutToUserUseCase.addWorkoutToUser(user.getId(), workout1);
        assertFalse(user.getWorkouts().isEmpty());

        //The workoutEntity stays the same before editing and after editing, since it is an invalid request
        WorkoutEntity expected = user.getWorkouts().getFirst();
        Workout editedWorkout = new Workout(null, "MMA", "doing some Boxing", now, new ArrayList<>(), user.getId());
        editedWorkout.setId(1232L);
        //test results:
        JPAWorkoutExerciseAdapterResult<Workout> actualResult1 = editWorkoutInUserUseCase.editWorkoutInUser(user.getId(), editedWorkout);
        JPAWorkoutExerciseAdapterResult<Workout> expectedResult =
                new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);
        assertResultEquals(expectedResult, actualResult1, Workout::getId);
        //test if the workouts in the user stays the same
        workoutEquals(expected, user.getWorkouts().getFirst());
    }


    @Test
    public void testInvalidEditWorkoutInUser2(){
        em.persist(user);
        workout1.setCreatedByUserId(user.getId());
        addWorkoutToUserUseCase.addWorkoutToUser(user.getId(), workout1);


        //test results:
        workout2.setId(9814L);
        JPAWorkoutExerciseAdapterResult<Workout> actualResult1 = editWorkoutInUserUseCase.editWorkoutInUser(9814L, workout2);
        JPAWorkoutExerciseAdapterResult<Workout> expectedResult1 =
                new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.UNAUTHORIZED);
        assertResultEquals(expectedResult1, actualResult1, Workout::getId);

        workout1.setCreatedByUserId(7301L);
        JPAWorkoutExerciseAdapterResult<Workout> actualResult2 = editWorkoutInUserUseCase.editWorkoutInUser(user.getWorkouts().getFirst().getId(), workout1);
        JPAWorkoutExerciseAdapterResult<Workout> expectedResult2 =
                new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);
        assertResultEquals(expectedResult2, actualResult2, Workout::getId);

        workout1.setId(user.getWorkouts().getFirst().getId());
        JPAWorkoutExerciseAdapterResult<Workout> actualResult3 = editWorkoutInUserUseCase.editWorkoutInUser(user.getWorkouts().getFirst().getId(), workout1);
        JPAWorkoutExerciseAdapterResult<Workout> expectedResult3 =
                new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.NO_PERMISSIONS);
        assertResultEquals(expectedResult3, actualResult3, Workout::getId);
    }
}
