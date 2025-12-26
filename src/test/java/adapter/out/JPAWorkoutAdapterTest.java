package adapter.out;

import adapter.out.Entities.*;
import application.port.out.UserPorts.AddWorkoutToUserPort;
import application.port.out.UserPorts.DeleteWorkoutInUserPort;
import application.port.out.UserPorts.EditWorkoutInUserPort;
import domain.Results.JPAWorkoutExerciseAdapterResult;
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

import static adapter.mapper.JPAWorkoutMapper.toDomain;
import static adapter.mapper.JPAUserMapper.toDomain;
import static adapter.out.ExerciseWorkoutAdapterUtils.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestTransaction
public class JPAWorkoutAdapterTest {
    @Inject
    AddWorkoutToUserPort addWorkoutToUserPort;
    @Inject
    DeleteWorkoutInUserPort deleteWorkoutInUserPort;
    @Inject
    EditWorkoutInUserPort editWorkoutInUserPort;

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
        JPAWorkoutExerciseAdapterResult<User> actualResult = addWorkoutToUserPort.addWorkoutToUser(user.getId(), workout1);
        WorkoutEntity expected = new WorkoutEntity(user.getWorkouts().getFirst().getId(), "Cardio", "doing cardio", now);
        expected.setOwner(user);
        //Test if workout is correctly saved in user:
        workoutEquals(expected, user.getWorkouts().getFirst());
        //Test if workout is saved in the DB:
        workoutEquals(expected, em.find(WorkoutEntity.class, user.getWorkouts().getFirst().getId()));
        //Test the result:
        JPAWorkoutExerciseAdapterResult<User> expectedResult = new JPAWorkoutExerciseAdapterResult.Success<>(toDomain(user));
        assertResultEquals(expectedResult, actualResult, User::getId);
    }


    @Test
    public void testAddWorkout2(){
        em.persist(user);
        JPAWorkoutExerciseAdapterResult<User> actualResult1 = addWorkoutToUserPort.addWorkoutToUser(user.getId(), workout1);
        JPAWorkoutExerciseAdapterResult<User> actualResult2 = addWorkoutToUserPort.addWorkoutToUser(user.getId(), workout2);
        JPAWorkoutExerciseAdapterResult<User> actualResult3 = addWorkoutToUserPort.addWorkoutToUser(user.getId(), workout3);

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
        JPAWorkoutExerciseAdapterResult<User> expectedResult =  new JPAWorkoutExerciseAdapterResult.Success<>(toDomain(user));
        assertResultEquals(expectedResult, actualResult1, User::getId);
        assertResultEquals(expectedResult, actualResult2, User::getId);
        assertResultEquals(expectedResult, actualResult3, User::getId);

    }

    @Test
    public void testAddSameWorkout(){
        em.persist(user);
        //should be possible. It is in the users responsibility
        JPAWorkoutExerciseAdapterResult<User> actualResult1 = addWorkoutToUserPort.addWorkoutToUser(user.getId(), workout1);
        JPAWorkoutExerciseAdapterResult<User> actualResult2 = addWorkoutToUserPort.addWorkoutToUser(user.getId(), workout1);

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
        JPAWorkoutExerciseAdapterResult<User>  expectedResult =  new JPAWorkoutExerciseAdapterResult.Success<>(toDomain(user));
        assertResultEquals(expectedResult, actualResult1, User::getId);
        assertResultEquals(expectedResult, actualResult2, User::getId);

    }

    @Test
    public void testAddUnknownWorkout(){
        em.persist(user);
        //workout1 is unknown to the DB, because a random key is set and it was never persisted before
        workout1.setId(2031L);
        JPAWorkoutExerciseAdapterResult<User> actualResult = addWorkoutToUserPort.addWorkoutToUser(user.getId(), workout1);
        //Test if workout is saved in the user:
        assertTrue(user.getWorkouts().isEmpty());
        //Test if workout is saved in the DB:
        assertNull(em.find(WorkoutEntity.class, workout1.getId()));
        //Test result:
        JPAWorkoutExerciseAdapterResult<User> expectedResult =
                new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);
        assertResultEquals(expectedResult, actualResult, User::getId);
    }

    @Test
    //A known workout (in DB) is newly copied into the user
    public void testAddKnownWorkout(){
        em.persist(user);
        WorkoutEntity knownWorkout = new WorkoutEntity(null, "Cardio", "doing cardio", now);
        knownWorkout.setOwner(user);
        em.persist(knownWorkout);
        JPAWorkoutExerciseAdapterResult<User> actualResult = addWorkoutToUserPort.addWorkoutToUser(user.getId(), toDomain(knownWorkout));
        //Test if the workout is saved in the User:
        WorkoutEntity expected = new WorkoutEntity(null, "Cardio", "doing cardio", now);
        expected.setOwner(user);
        assertNotNull(user.getWorkouts().getFirst());
        //Is not equal since they don't have the same PKs
        assertThrows(AssertionError.class, () -> workoutEqualsWithKey(expected, user.getWorkouts().getFirst()));
        //However the attributes should be equal:
        workoutEquals(expected, user.getWorkouts().getFirst());
        //test results:
        JPAWorkoutExerciseAdapterResult<User> expectedResult =  new JPAWorkoutExerciseAdapterResult.Success<>(toDomain(user));
        assertResultEquals(expectedResult, actualResult, User::getId);
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
        JPAWorkoutExerciseAdapterResult<User> actualResult = addWorkoutToUserPort.addWorkoutToUser(user.getId(), workout1);
        //Test if the invalid workout is even saved in user
        assertTrue(user.getWorkouts().isEmpty());
        //Test result:
        JPAWorkoutExerciseAdapterResult<User> expectedResult =
                new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);
        assertResultEquals(expectedResult, actualResult, User::getId);
    }
    @Test
    //this should not save any exercise to User since it is a newly created workout with exercises.
    // This is not possible. The user has to create a new empty workout first and then add exercises: see in JPAWorkoutAdapter
    public void testAddInvalidWorkoutWithExercise2(){
        em.persist(user);
        ExerciseEntity exerciseEntity = new ExerciseEntity(null, "Bench press", "push", 600L);
        exerciseEntity.setOwner(user);

        //Known exercise to the DB
        em.persist(exerciseEntity);
        workout1.setCreatedByUserId(user.getId());
        workout1.getExercises().add(exerciseEntity.getId());
        JPAWorkoutExerciseAdapterResult<User> actualResult = addWorkoutToUserPort.addWorkoutToUser(user.getId(), workout1);
        //Test if workout is saved in User
        assertTrue(user.getWorkouts().isEmpty());
        //test results:
        JPAWorkoutExerciseAdapterResult<User> expectedResult =
                new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);
        assertResultEquals(expectedResult, actualResult, User::getId);
    }


    @Test
    //prerequisites: both workout and exercise are saved in DB
    public void testAddValidWorkoutWithExercise(){
        em.persist(user);

        ExerciseEntity exerciseEntity = new ExerciseEntity(null, "Bench press", "push", 600L);
        exerciseEntity.setOwner(user);
        WorkoutEntity workoutEntity = new WorkoutEntity(null, "Cardio",  "doing cardio", now);
        workoutEntity.setOwner(user);
        workoutEntity.getExercises().add(exerciseEntity);
        //Known workout and exercise to the DB
        em.persist(workoutEntity);
        workout1.setCreatedByUserId(user.getId());
        workout1.getExercises().add(exerciseEntity.getId());
        workout1.setId(workoutEntity.getId());

        JPAWorkoutExerciseAdapterResult<User> actualResult = addWorkoutToUserPort.addWorkoutToUser(user.getId(), workout1);
        //Test if workout is saved in User:
        WorkoutEntity expected = new WorkoutEntity(user.getWorkouts().getFirst().getId(), "Cardio", "doing cardio", now);
        expected.setOwner(user);
        expected.getExercises().add(exerciseEntity);
        workoutEquals(expected, user.getWorkouts().getFirst());
        assertNotEquals(exerciseEntity.getId(), user.getWorkouts().getFirst().getExercises().getFirst().getId());
        //Test results:
        JPAWorkoutExerciseAdapterResult<User> expectedResult =
                new JPAWorkoutExerciseAdapterResult.Success<>(toDomain(user));
        assertResultEquals(expectedResult, actualResult, User::getId);
        //check if both workouts (the known and copied workout in user) are in the DB
        Long count = em.createQuery("SELECT COUNT(w) FROM WorkoutEntity w", Long.class).getSingleResult();
        assertEquals(2, count);
    }

    @Test
    public void testAddWorkoutWithWrongUserId(){
        em.persist(user);
        JPAWorkoutExerciseAdapterResult<User> actualResult = addWorkoutToUserPort.addWorkoutToUser(1231L, workout1);
        //test if workout is saved in user:
        assertTrue(user.getWorkouts().isEmpty());
        //test result:
        JPAWorkoutExerciseAdapterResult<User> expectedResult =
                new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.USER_NOT_FOUND);
        assertResultEquals(expectedResult, actualResult, User::getId);

    }

    //DELETE WORKOUT

    @Test
    public void testDeleteWorkoutInUser(){
        em.persist(user);
        addWorkoutToUserPort.addWorkoutToUser(user.getId(), workout1);
        assertFalse(user.getWorkouts().isEmpty());
        JPAWorkoutExerciseAdapterResult<User> actualResult = deleteWorkoutInUserPort.deleteWorkoutInUser(user.getId(), user.getWorkouts().getFirst().getId());
        //test if workout is deleted in the user:
        assertTrue(user.getWorkouts().isEmpty());
        //test result
        JPAWorkoutExerciseAdapterResult<User> expectedResult = new JPAWorkoutExerciseAdapterResult.Success<>(toDomain(user));
        assertResultEquals(expectedResult, actualResult, User::getId);
        //check if there is no WorkoutEntity in the DB
        Long count = em.createQuery("SELECT COUNT(w) FROM WorkoutEntity w", Long.class).getSingleResult();
        assertEquals(0, count);
    }

    @Test
    public void testDeleteWorkoutInUser2(){
        em.persist(user);
        addWorkoutToUserPort.addWorkoutToUser(user.getId(), workout1);
        addWorkoutToUserPort.addWorkoutToUser(user.getId(), workout2);
        assertFalse(user.getWorkouts().isEmpty());
        JPAWorkoutExerciseAdapterResult<User> actualResult = deleteWorkoutInUserPort.deleteWorkoutInUser(user.getId(), user.getWorkouts().getFirst().getId());
        //test if workout is deleted in the user:
        WorkoutEntity wEntityInList = new WorkoutEntity(null, "Upper body", "Everything related to upper body", now);
        wEntityInList.setOwner(user);
        assertFalse(user.getWorkouts().isEmpty());
        assertEquals(1, user.getWorkouts().size());
        workoutEquals(wEntityInList, user.getWorkouts().getFirst());
        //test results
        JPAWorkoutExerciseAdapterResult<User> expectedResult = new JPAWorkoutExerciseAdapterResult.Success<>(toDomain(user));
        assertResultEquals(expectedResult, actualResult, User::getId);
        //check if there is no WorkoutEntity in the DB
        Long count = em.createQuery("SELECT COUNT(w) FROM WorkoutEntity w", Long.class).getSingleResult();
        assertEquals(1, count);
    }

    @Test
    public void testDeleteWorkoutWithExercises(){
        em.persist(user);

        ExerciseEntity exerciseEntity = new ExerciseEntity(null, "Bench press", "push", 600L);
        exerciseEntity.setOwner(user);
        WorkoutEntity workoutEntity = new WorkoutEntity(null, "Cardio",  "doing cardio", now);
        workoutEntity.setOwner(user);
        workoutEntity.getExercises().add(exerciseEntity);
        //Known workout and exercise to the DB
        em.persist(workoutEntity);
        workout1.setCreatedByUserId(user.getId());
        workout1.getExercises().add(exerciseEntity.getId());
        workout1.setId(workoutEntity.getId());
        addWorkoutToUserPort.addWorkoutToUser(user.getId(), workout1);
        assertFalse(user.getWorkouts().isEmpty());

       //Test: how many entities are in the DB after saving one additional WorkoutEntity to User
        Long workoutCount = em.createQuery("SELECT COUNT(w) FROM WorkoutEntity w", Long.class).getSingleResult();
        assertEquals(2, workoutCount);
        Long exerciseCount = em.createQuery("SELECT COUNT(e) FROM ExerciseEntity e", Long.class).getSingleResult();
        assertEquals(2, exerciseCount);

        //Delete the workout in user
        JPAWorkoutExerciseAdapterResult<User> actualResult = deleteWorkoutInUserPort.deleteWorkoutInUser(user.getId(), user.getWorkouts().getFirst().getId());

        //Test if workout in user is deleted
        assertTrue(user.getWorkouts().isEmpty());

        //Test if workout AND the exercise are deleted from the DB
        workoutCount = em.createQuery("SELECT COUNT(w) FROM WorkoutEntity w", Long.class).getSingleResult();
        assertEquals(1, workoutCount);
        exerciseCount = em.createQuery("SELECT COUNT(e) FROM ExerciseEntity e", Long.class).getSingleResult();
        assertEquals(1, exerciseCount);
        //Test result
        JPAWorkoutExerciseAdapterResult<User> expectedResult = new JPAWorkoutExerciseAdapterResult.Success<>(toDomain(user));
        assertResultEquals(expectedResult, actualResult, User::getId);
    }

    @Test
    public void testDeleteWorkoutWithInvalidIds(){
        em.persist(user);
        assertTrue(user.getWorkouts().isEmpty());
        //test results
        JPAWorkoutExerciseAdapterResult<User> actualResult1 = deleteWorkoutInUserPort.deleteWorkoutInUser(user.getId(), user.getId());
        JPAWorkoutExerciseAdapterResult<User>  expectedResult1 =
                new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.WORKOUT_IN_USER_NOT_FOUND);
        assertResultEquals(expectedResult1, actualResult1, User::getId);

        JPAWorkoutExerciseAdapterResult<User> actualResult2 = deleteWorkoutInUserPort.deleteWorkoutInUser(10L, 10L);
        JPAWorkoutExerciseAdapterResult<User> expectedResult2 =
                new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.USER_NOT_FOUND);
        assertResultEquals(expectedResult2, actualResult2, User::getId);

        addWorkoutToUserPort.addWorkoutToUser(user.getId(), workout1);
        assertFalse(user.getWorkouts().isEmpty());
        JPAWorkoutExerciseAdapterResult<User> actualResult3 = deleteWorkoutInUserPort.deleteWorkoutInUser(user.getId(), 100L);
        JPAWorkoutExerciseAdapterResult<User> expectedResult3 =
                new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.WORKOUT_IN_USER_NOT_FOUND);
        assertResultEquals(expectedResult3, actualResult3, User::getId);
        assertFalse(user.getWorkouts().isEmpty());
    }

    //EDIT WORKOUT

    @Test
    public void testEditWorkoutInUser(){
        em.persist(user);
        addWorkoutToUserPort.addWorkoutToUser(user.getId(), workout1);
        assertFalse(user.getWorkouts().isEmpty());

        Workout editedWorkout = new Workout(null, "strength training", "training for strength", now, new ArrayList<>(), user.getId());
        editedWorkout.setId(user.getWorkouts().getFirst().getId());
        JPAWorkoutExerciseAdapterResult<Workout> actualResult = editWorkoutInUserPort.editWorkoutInUser(user.getWorkouts().getFirst().getId(), editedWorkout);

        WorkoutEntity expectedAfterEditing = new WorkoutEntity(user.getWorkouts().getFirst().getId(),  "strength training", "training for strength", now);
        expectedAfterEditing.setId(editedWorkout.getId());
        expectedAfterEditing.setOwner(user);
        //test if the workout has been edited in the user
        workoutEquals(expectedAfterEditing, user.getWorkouts().getFirst());
        //test results
        JPAWorkoutExerciseAdapterResult<Workout> expectedResult = new JPAWorkoutExerciseAdapterResult.Success<>(toDomain(user.getWorkouts().getFirst()));
        assertResultEquals(expectedResult, actualResult, Workout::getId);
    }

    @Test
    public void testEditWorkoutInUser2(){
        em.persist(user);
        addWorkoutToUserPort.addWorkoutToUser(user.getId(), workout1);
        assertFalse(user.getWorkouts().isEmpty());

        Workout editedWorkout = new Workout(null, null, "training for strength", now, new ArrayList<>(), user.getId());
        editedWorkout.setId(user.getWorkouts().getFirst().getId());
        JPAWorkoutExerciseAdapterResult<Workout> actualResult = editWorkoutInUserPort.editWorkoutInUser(user.getWorkouts().getFirst().getId(), editedWorkout);

        WorkoutEntity expectedAfterEditing = new WorkoutEntity(user.getWorkouts().getFirst().getId(),  "Cardio", "training for strength", now);
        expectedAfterEditing.setId(editedWorkout.getId());
        expectedAfterEditing.setOwner(user);
        //test if the workout has been edited in the user
        workoutEquals(expectedAfterEditing, user.getWorkouts().getFirst());
        //test results
        JPAWorkoutExerciseAdapterResult<Workout> expectedResult = new JPAWorkoutExerciseAdapterResult.Success<>(toDomain(user.getWorkouts().getFirst()));
        assertResultEquals(expectedResult, actualResult, Workout::getId);
    }

    @Test
    public void testEditWorkoutInUser3(){
        em.persist(user);
        addWorkoutToUserPort.addWorkoutToUser(user.getId(), workout1);
        addWorkoutToUserPort.addWorkoutToUser(user.getId(), workout2);
        assertFalse(user.getWorkouts().isEmpty());

        Workout editedWorkout1 = new Workout(null, "strength training", "training for strength", now, new ArrayList<>(), user.getId());
        editedWorkout1.setId(user.getWorkouts().getFirst().getId());
        Workout editedWorkout2 = new Workout(null, "MMA", "doing some Boxing", now, new ArrayList<>(), user.getId());
        editedWorkout2.setId(user.getWorkouts().get(1).getId());
        JPAWorkoutExerciseAdapterResult<Workout> actualResult1 = editWorkoutInUserPort.editWorkoutInUser(user.getWorkouts().getFirst().getId(), editedWorkout1);
        JPAWorkoutExerciseAdapterResult<Workout> actualResult2 = editWorkoutInUserPort.editWorkoutInUser(user.getWorkouts().get(1).getId(), editedWorkout2);

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
        JPAWorkoutExerciseAdapterResult<Workout> expectedResult1 = new JPAWorkoutExerciseAdapterResult.Success<>(toDomain(user.getWorkouts().getFirst()));
        JPAWorkoutExerciseAdapterResult<Workout> expectedResult2 = new JPAWorkoutExerciseAdapterResult.Success<>(toDomain(user.getWorkouts().get(1)));
        assertResultEquals(expectedResult1, actualResult1, Workout::getId);
        assertResultEquals(expectedResult2, actualResult2, Workout::getId);

    }

    @Test
    public void testInvalidEditWorkoutInUser(){
        em.persist(user);
        addWorkoutToUserPort.addWorkoutToUser(user.getId(), workout1);
        assertFalse(user.getWorkouts().isEmpty());

        //The workoutEntity stays the same before editing and after editing, since it is an invalid request
        WorkoutEntity expected = user.getWorkouts().getFirst();
        Workout editedWorkout = new Workout(null, "MMA", "doing some Boxing", now, new ArrayList<>(), user.getId());
        editedWorkout.setId(1232L);
        //test results:
        JPAWorkoutExerciseAdapterResult<Workout> actualResult1 = editWorkoutInUserPort.editWorkoutInUser(user.getId(), editedWorkout);
        JPAWorkoutExerciseAdapterResult<Workout> expectedResult =
                new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);
        assertResultEquals(expectedResult, actualResult1, Workout::getId);
        //test if the workouts in the user stays the same
        workoutEquals(expected, user.getWorkouts().getFirst());
    }


    @Test
    public void testInvalidEditWorkoutInUser2(){
        em.persist(user);
        addWorkoutToUserPort.addWorkoutToUser(user.getId(), workout1);


        //test results:
        workout2.setId(9814L);
        JPAWorkoutExerciseAdapterResult<Workout> actualResult1 = editWorkoutInUserPort.editWorkoutInUser(9814L, workout2);
        JPAWorkoutExerciseAdapterResult<Workout> expectedResult1 =
                new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.WORKOUT_NOT_FOUND);
        assertResultEquals(expectedResult1, actualResult1, Workout::getId);

        workout1.setCreatedByUserId(7301L);
        JPAWorkoutExerciseAdapterResult<Workout> actualResult2 = editWorkoutInUserPort.editWorkoutInUser(user.getWorkouts().getFirst().getId(), workout1);
        JPAWorkoutExerciseAdapterResult<Workout> expectedResult2 =
                new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);
        assertResultEquals(expectedResult2, actualResult2, Workout::getId);
    }
}
