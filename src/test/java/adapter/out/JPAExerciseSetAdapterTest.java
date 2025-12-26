package adapter.out;

import adapter.out.Entities.CoachEntity;
import adapter.out.Entities.ExerciseEntity;
import adapter.out.Entities.ExerciseSetEntity;
import adapter.out.Entities.UserEntity;
import application.port.out.UserPorts.AddExerciseSetToExercisePort;
import application.port.out.UserPorts.DeleteExerciseSetInExercisePort;
import application.port.out.UserPorts.EditExerciseSetPort;
import domain.Results.JPAWorkoutExerciseAdapterResult;
import domain.model.Exercise;
import domain.model.ExerciseSet;
import domain.valueobject.Gender;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static adapter.mapper.JPAExerciseSetMapper.toDomain;
import static adapter.mapper.JPAExerciseMapper.toDomain;
import static adapter.out.ExerciseWorkoutAdapterUtils.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestTransaction
public class JPAExerciseSetAdapterTest {
    @Inject
    EntityManager em;
    @Inject
    AddExerciseSetToExercisePort  addExerciseSetToExercisePort;
    @Inject
    DeleteExerciseSetInExercisePort deleteExerciseSetInExercisePort;
    @Inject
    EditExerciseSetPort  editExerciseSetPort;

    //ADD EXERCISE SET

    LocalDateTime now1;
    LocalDateTime now2;
    LocalDateTime now3;
    ExerciseEntity exerciseEntity1;
    ExerciseEntity exerciseEntity2;
    ExerciseEntity exerciseEntity3;
    ExerciseSet exerciseSet1;
    ExerciseSet exerciseSet2;
    UserEntity user;

    @BeforeEach
    public void setup() {
        now1 = LocalDateTime.now();
        now2 = LocalDateTime.now();
        now3 = LocalDateTime.now();
        exerciseSet1 = new ExerciseSet(null, 10, 15D, "smooth", 200L, now1, null);
        exerciseSet2 = new ExerciseSet(null, 8, 20.5, "rough", 100L, now1, null);
        exerciseEntity1 = new ExerciseEntity(null, "Cardio", "doing cardio", 600L);
        exerciseEntity2 = new ExerciseEntity(null, "MMA", "boxing", 1200L);
        exerciseEntity3 = new ExerciseEntity(null, "gym", "strength training", 700L);
        user = new CoachEntity(null, "firstName", "lastName", "email", "passsword",
                Gender.MALE, LocalDate.of(2000, 2, 10), now1);
    }

    @Test
    public void testAddExerciseSetToExercise() {
        em.persist(user);
        exerciseEntity1.setOwner(user);
        em.persist(exerciseEntity1);
        exerciseSet1.setBelongsToExercise(exerciseEntity1.getId());

        JPAWorkoutExerciseAdapterResult<Exercise> actualResult =
                addExerciseSetToExercisePort.addExerciseSetToExercise(exerciseEntity1.getId(), exerciseSet1);
        //test if exerciseSet is added in exercise
        assertFalse(exerciseEntity1.getExerciseSets().isEmpty());
        assertEquals(1, exerciseEntity1.getExerciseSets().size());
        ExerciseSetEntity expected = new ExerciseSetEntity(exerciseEntity1.getExerciseSets().getFirst().getId(), 10, 15D,
                "smooth", 200L, now1);
        expected.setExercise(exerciseEntity1);
        exerciseSetEqualsWithKey(expected, exerciseEntity1.getExerciseSets().getFirst());
        //test results:
        JPAWorkoutExerciseAdapterResult<Exercise> expectedResult =
                new JPAWorkoutExerciseAdapterResult.Success<>(toDomain(exerciseEntity1));
        assertResultEquals(expectedResult, actualResult, Exercise::getId);
        //test is there is one set in the DB:
        Long count = em.createQuery("SELECT COUNT(e) FROM ExerciseSetEntity e", Long.class).getSingleResult();
        assertEquals(1, count);
    }

    @Test
    public void testAddExerciseSetToExercise2() {
        em.persist(user);
        exerciseEntity1.setOwner(user);
        em.persist(exerciseEntity1);

        exerciseSet1.setBelongsToExercise(exerciseEntity1.getId());
        exerciseSet2.setBelongsToExercise(exerciseEntity1.getId());

        JPAWorkoutExerciseAdapterResult<Exercise> actualResult1 =
                addExerciseSetToExercisePort.addExerciseSetToExercise(exerciseEntity1.getId(), exerciseSet1);
        JPAWorkoutExerciseAdapterResult<Exercise> actualResult2 =
                addExerciseSetToExercisePort.addExerciseSetToExercise(exerciseEntity1.getId(), exerciseSet2);
        //test if both sets are in exerciseEntity1
        assertFalse(exerciseEntity1.getExerciseSets().isEmpty());
        assertEquals(2, exerciseEntity1.getExerciseSets().size());

        ExerciseSetEntity expected1 = new ExerciseSetEntity(exerciseEntity1.getExerciseSets().getFirst().getId(), 10, 15D,
                "smooth", 200L, now1);
        expected1.setExercise(exerciseEntity1);
        ExerciseSetEntity expected2 = new ExerciseSetEntity(exerciseEntity1.getExerciseSets().get(1).getId(), 8, 20.5,
                "rough", 100L, now1);
        expected2.setExercise(exerciseEntity1);

        exerciseSetEqualsWithKey(expected1, exerciseEntity1.getExerciseSets().getFirst());
        exerciseSetEqualsWithKey(expected2, exerciseEntity1.getExerciseSets().get(1));
        //test results
        JPAWorkoutExerciseAdapterResult<Exercise> expectedResult1 =
                new  JPAWorkoutExerciseAdapterResult.Success<>(toDomain(exerciseEntity1));
        JPAWorkoutExerciseAdapterResult<Exercise> expectedResult2 =
                new JPAWorkoutExerciseAdapterResult.Success<>(toDomain(exerciseEntity1));
        assertResultEquals(expectedResult1, actualResult1, Exercise::getId);
        assertResultEquals(expectedResult2, actualResult2, Exercise::getId);
        //test if there are two ExerciseSets in the DB
        Long count = em.createQuery("SELECT COUNT(e) FROM ExerciseSetEntity e", Long.class).getSingleResult();
        assertEquals(2, count);
    }

    @Test
    public void testAddExerciseSetToExercise3() {
        em.persist(user);
        exerciseEntity1.setOwner(user);
        em.persist(exerciseEntity1);
        exerciseSet1.setBelongsToExercise(exerciseEntity1.getId());

        JPAWorkoutExerciseAdapterResult<Exercise> actualResult1 =
                addExerciseSetToExercisePort.addExerciseSetToExercise(exerciseEntity1.getId(), exerciseSet1);

        JPAWorkoutExerciseAdapterResult<Exercise> actualResult2 =
                addExerciseSetToExercisePort.addExerciseSetToExercise(exerciseEntity1.getId(), exerciseSet1);
        //check if the same exerciseSet was added twice in exercise (should be possible)
        assertFalse(exerciseEntity1.getExerciseSets().isEmpty());
        assertEquals(2, exerciseEntity1.getExerciseSets().size());
        ExerciseSetEntity expected1 = new ExerciseSetEntity(exerciseEntity1.getExerciseSets().getFirst().getId(), 10, 15D,
                "smooth", 200L, now1);
        expected1.setExercise(exerciseEntity1);

        ExerciseSetEntity expected2 = new ExerciseSetEntity(exerciseEntity1.getExerciseSets().get(1).getId(), 10, 15D,
                "smooth", 200L, now1);
        expected2.setExercise(exerciseEntity1);
        exerciseSetEqualsWithKey(expected1, exerciseEntity1.getExerciseSets().getFirst());
        exerciseSetEqualsWithKey(expected2, exerciseEntity1.getExerciseSets().get(1));
        //expected1 and expected2 are equals attributes wise
        exerciseSetEquals(expected1, expected2);
        //but now key wise
        assertThrows(AssertionError.class, () -> exerciseSetEqualsWithKey(expected1, expected2));
        //check if there are two exerciseSets in the DB
        Long count = em.createQuery("SELECT COUNT(e) FROM ExerciseSetEntity e", Long.class).getSingleResult();
        assertEquals(2, count);
    }

    @Test
    public void testAddInvalidExerciseSetToExercise() {
        em.persist(user);
        exerciseEntity1.setOwner(user);
        em.persist(exerciseEntity1);

        exerciseSet1.setBelongsToExercise(4534872L);
        //invalid ids
        JPAWorkoutExerciseAdapterResult<Exercise> actualResul1 =
                addExerciseSetToExercisePort.addExerciseSetToExercise(exerciseEntity1.getId(), exerciseSet1);

        exerciseSet1.setBelongsToExercise(exerciseEntity1.getId());
        JPAWorkoutExerciseAdapterResult<Exercise> actualResult2 =
                addExerciseSetToExercisePort.addExerciseSetToExercise(98157L, exerciseSet1);

        JPAWorkoutExerciseAdapterResult<Exercise> actualResult3 =
                addExerciseSetToExercisePort.addExerciseSetToExercise(null, null);

        exerciseSet1.setBelongsToExercise(40872L);
        JPAWorkoutExerciseAdapterResult<Exercise> actualResult4 =
                addExerciseSetToExercisePort.addExerciseSetToExercise(40872L, exerciseSet1);

        //check if there is an invalid exerciseSet saved in exercise
        assertTrue(exerciseEntity1.getExerciseSets().isEmpty());
        //check results
        JPAWorkoutExerciseAdapterResult<Exercise> expectedResult1 =
                new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);

        JPAWorkoutExerciseAdapterResult<Exercise> expectedResult2 =
                new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);

        JPAWorkoutExerciseAdapterResult<Exercise> expectedResult3 =
                new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);

        JPAWorkoutExerciseAdapterResult<Exercise> expectedResult4 =
                new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_NOT_FOUND);

        assertResultEquals(expectedResult1, actualResul1, Exercise::getId);
        assertResultEquals(expectedResult2, actualResult2, Exercise::getId);
        assertResultEquals(expectedResult3, actualResult3, Exercise::getId);
        assertResultEquals(expectedResult4, actualResult4, Exercise::getId);
        //check if there are no exerciseSets in the DB
        Long count = em.createQuery("SELECT COUNT(e) FROM ExerciseSetEntity e", Long.class).getSingleResult();
        assertEquals(0, count);
    }

    //DELETE EXERCISE SET IN EXERCISE

    @Test
    public void testDeleteExerciseSetFromExercise() {
        em.persist(user);
        exerciseEntity1.setOwner(user);
        em.persist(exerciseEntity1);
        exerciseSet1.setBelongsToExercise(exerciseEntity1.getId());
        addExerciseSetToExercisePort.addExerciseSetToExercise(exerciseEntity1.getId(), exerciseSet1);
        //delete exerciseSet
        JPAWorkoutExerciseAdapterResult<Exercise> actualResult =
                deleteExerciseSetInExercisePort.deleteExerciseSetInExercise(exerciseEntity1.getId(), exerciseEntity1.getExerciseSets().getFirst().getId());
        System.out.println(actualResult.toString());

        //check if exerciseSet was deleted
        assertTrue(exerciseEntity1.getExerciseSets().isEmpty());
        //check results
        JPAWorkoutExerciseAdapterResult<Exercise> expectedResult =
                new JPAWorkoutExerciseAdapterResult.Success<>(toDomain(exerciseEntity1));

        assertResultEquals(expectedResult, actualResult,  Exercise::getId);
        //check if there is no exerciseSet in the DB
        Long count = em.createQuery("SELECT COUNT(e) FROM ExerciseSetEntity e", Long.class).getSingleResult();
        assertEquals(0, count);
    }

    @Test
    public void testDeleteExerciseSetFromExercise2() {
        em.persist(user);
        exerciseEntity1.setOwner(user);
        em.persist(exerciseEntity1);
        exerciseSet1.setBelongsToExercise(exerciseEntity1.getId());
        addExerciseSetToExercisePort.addExerciseSetToExercise(exerciseEntity1.getId(), exerciseSet1);
        addExerciseSetToExercisePort.addExerciseSetToExercise(exerciseEntity1.getId(), exerciseSet1);

        //delete exerciseSet
        JPAWorkoutExerciseAdapterResult<Exercise> actualResult1 =
                deleteExerciseSetInExercisePort.deleteExerciseSetInExercise(exerciseEntity1.getId(), exerciseEntity1.getExerciseSets().getFirst().getId());
        JPAWorkoutExerciseAdapterResult<Exercise> actualResult2 =
                deleteExerciseSetInExercisePort.deleteExerciseSetInExercise(exerciseEntity1.getId(), exerciseEntity1.getExerciseSets().getFirst().getId());

        //check if both exerciseSets have been deleted from exercise
        assertTrue(exerciseEntity1.getExerciseSets().isEmpty());
        //check results
        JPAWorkoutExerciseAdapterResult<Exercise> expectedResult1 =
                new JPAWorkoutExerciseAdapterResult.Success<>(toDomain(exerciseEntity1));
        JPAWorkoutExerciseAdapterResult<Exercise> expectedResult2 =
                new JPAWorkoutExerciseAdapterResult.Success<>(toDomain(exerciseEntity1));

        assertResultEquals(expectedResult1, actualResult1, Exercise::getId);
        assertResultEquals(expectedResult2, actualResult2, Exercise::getId);
        //check if there are no exerciseSets in the DB:
        Long count = em.createQuery("SELECT COUNT(e) FROM ExerciseSetEntity e", Long.class).getSingleResult();
        assertEquals(0, count);
    }

    @Test
    public void testDeleteInvalidExerciseSetFromExercise() {
        em.persist(user);
        exerciseEntity1.setOwner(user);
        em.persist(exerciseEntity1);
        exerciseSet1.setBelongsToExercise(exerciseEntity1.getId());
        addExerciseSetToExercisePort.addExerciseSetToExercise(exerciseEntity1.getId(), exerciseSet1);

        JPAWorkoutExerciseAdapterResult<Exercise> actualResult1 =
                deleteExerciseSetInExercisePort.deleteExerciseSetInExercise(null, null);

        JPAWorkoutExerciseAdapterResult<Exercise> actualResult2 =
                deleteExerciseSetInExercisePort.deleteExerciseSetInExercise(71256L, exerciseEntity1.getExerciseSets().getFirst().getId());

        JPAWorkoutExerciseAdapterResult<Exercise> actualResult3 =
                deleteExerciseSetInExercisePort.deleteExerciseSetInExercise(exerciseEntity1.getId(), 8629561L);
        //check if the exerciseSets in exercise have NOT been deleted
        assertFalse(exerciseEntity1.getExerciseSets().isEmpty());
        //check results:
        JPAWorkoutExerciseAdapterResult<Exercise> expectedResult1 =
                new  JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);

        JPAWorkoutExerciseAdapterResult<Exercise> expectedResult2 =
                new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_NOT_FOUND);

        JPAWorkoutExerciseAdapterResult<Exercise> expectedResult3 =
                new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_SET_IN_EXERCISE_NOT_FOUND);

        assertResultEquals(expectedResult1, actualResult1, Exercise::getId);
        assertResultEquals(expectedResult2, actualResult2, Exercise::getId);
        assertResultEquals(expectedResult3, actualResult3, Exercise::getId);
        //check if there is one exerciseSet in the DB:
        Long count = em.createQuery("SELECT COUNT(e) FROM ExerciseSetEntity e", Long.class).getSingleResult();
        assertEquals(1, count);
    }

    @Test
    public void testEditExerciseSetInExercise() {
        em.persist(user);
        exerciseEntity1.setOwner(user);
        em.persist(exerciseEntity1);
        exerciseSet1.setBelongsToExercise(exerciseEntity1.getId());
        addExerciseSetToExercisePort.addExerciseSetToExercise(exerciseEntity1.getId(), exerciseSet1);

        ExerciseSet toBeEdited = new ExerciseSet(null, 11, 22D, "nothing", 234L,
                now2, exerciseEntity1.getId());
        toBeEdited.setId(exerciseEntity1.getExerciseSets().getFirst().getId());
        toBeEdited.setBelongsToExercise(exerciseEntity1.getId());
        //check if the exerciseSet was edited in exercise
        JPAWorkoutExerciseAdapterResult<ExerciseSet> actualResult =
                editExerciseSetPort.editExerciseSet(exerciseEntity1.getExerciseSets().getFirst().getId(), toBeEdited);

        assertEquals(1, exerciseEntity1.getExerciseSets().size());

        ExerciseSetEntity expected = new ExerciseSetEntity(exerciseEntity1.getExerciseSets().getFirst().getId(), 11, 22D,
                "nothing", 234L, now2);
        expected.setExercise(exerciseEntity1);
        exerciseSetEqualsWithKey(expected, exerciseEntity1.getExerciseSets().getFirst());
        //check results
        JPAWorkoutExerciseAdapterResult<ExerciseSet> expectedResult =
                new JPAWorkoutExerciseAdapterResult.Success<>(toDomain(exerciseEntity1.getExerciseSets().getFirst()));
        assertResultEquals(expectedResult, actualResult,  ExerciseSet::getId);
        //check if there is one exerciseSet in the DB:
        Long count = em.createQuery("SELECT COUNT(e) FROM ExerciseSetEntity e", Long.class).getSingleResult();
        assertEquals(1, count);
    }

    @Test
    public void testEditExerciseSetInExercise2() {
        em.persist(user);
        exerciseEntity1.setOwner(user);
        em.persist(exerciseEntity1);
        exerciseSet1.setBelongsToExercise(exerciseEntity1.getId());
        exerciseSet2.setBelongsToExercise(exerciseEntity1.getId());
        addExerciseSetToExercisePort.addExerciseSetToExercise(exerciseEntity1.getId(), exerciseSet1);
        addExerciseSetToExercisePort.addExerciseSetToExercise(exerciseEntity1.getId(), exerciseSet2);
        assertEquals(2,  exerciseEntity1.getExerciseSets().size());

        ExerciseSet toBeEdited1 = new ExerciseSet(null, 11, 22D, "nothing", 234L,
                now2, exerciseEntity1.getId());
        toBeEdited1.setId(exerciseEntity1.getExerciseSets().getFirst().getId());
        toBeEdited1.setBelongsToExercise(exerciseEntity1.getId());

        ExerciseSet toBeEdited2 = new ExerciseSet(null, 7, 22.5D, "good", 350L,
                now3, exerciseEntity1.getId());
        toBeEdited2.setId(exerciseEntity1.getExerciseSets().get(1).getId());
        toBeEdited2.setBelongsToExercise(exerciseEntity1.getId());

        JPAWorkoutExerciseAdapterResult<ExerciseSet> actualResult1 =
                editExerciseSetPort.editExerciseSet(exerciseEntity1.getExerciseSets().getFirst().getId(), toBeEdited1);

        JPAWorkoutExerciseAdapterResult<ExerciseSet> actualResult2 =
                editExerciseSetPort.editExerciseSet(exerciseEntity1.getExerciseSets().get(1).getId(), toBeEdited2);

        //check if the exerciseSets were edited in exercise
        ExerciseSetEntity expected1 = new  ExerciseSetEntity(exerciseEntity1.getExerciseSets().getFirst().getId(), 11, 22D,
                "nothing", 234L, now2);
        ExerciseSetEntity expected2 = new ExerciseSetEntity(exerciseEntity1.getExerciseSets().get(1).getId(), 7, 22.5D,
                "good", 350L, now3);
        exerciseSetEqualsWithKey(expected1, exerciseEntity1.getExerciseSets().getFirst());
        exerciseSetEqualsWithKey(expected2, exerciseEntity1.getExerciseSets().get(1));
        //check results:
        JPAWorkoutExerciseAdapterResult<ExerciseSet> expectedResult1 =
                new JPAWorkoutExerciseAdapterResult.Success<>(toDomain(exerciseEntity1.getExerciseSets().getFirst()));
        JPAWorkoutExerciseAdapterResult<ExerciseSet> expectedResult2 =
                new JPAWorkoutExerciseAdapterResult.Success<>(toDomain(exerciseEntity1.getExerciseSets().get(1)));
        assertResultEquals(expectedResult1, actualResult1, ExerciseSet::getId);
        assertResultEquals(expectedResult2, actualResult2, ExerciseSet::getId);
        //check if there are two exerciseSets in the DB:
        Long count = em.createQuery("SELECT COUNT(e) FROM ExerciseSetEntity e", Long.class).getSingleResult();
        assertEquals(2, count);
    }

    @Test
    //edit an exerciseSet twice
    public void testEditExerciseSetInExercise3() {
        em.persist(user);
        exerciseEntity1.setOwner(user);
        em.persist(exerciseEntity1);
        exerciseSet1.setBelongsToExercise(exerciseEntity1.getId());
        addExerciseSetToExercisePort.addExerciseSetToExercise(exerciseEntity1.getId(), exerciseSet1);

        ExerciseSet toBeEdited1 = new ExerciseSet(null, 2, 40D, "best", 40L,
                now1, exerciseEntity1.getId());
        toBeEdited1.setId(exerciseEntity1.getExerciseSets().getFirst().getId());
        toBeEdited1.setBelongsToExercise(exerciseEntity1.getId());

        ExerciseSet toBeEdited2 = new ExerciseSet(null, 11, 22D, "nothing", 234L,
                now2, exerciseEntity1.getId());
        toBeEdited2.setId(exerciseEntity1.getExerciseSets().getFirst().getId());
        toBeEdited2.setBelongsToExercise(exerciseEntity1.getId());
        //check if the exerciseSet was edited in exercise
        JPAWorkoutExerciseAdapterResult<ExerciseSet> actualResult1 =
                editExerciseSetPort.editExerciseSet(exerciseEntity1.getExerciseSets().getFirst().getId(), toBeEdited1);

        JPAWorkoutExerciseAdapterResult<ExerciseSet> actualResult2 =
                editExerciseSetPort.editExerciseSet(exerciseEntity1.getExerciseSets().getFirst().getId(), toBeEdited2);

        assertEquals(1, exerciseEntity1.getExerciseSets().size());

        ExerciseSetEntity expected = new ExerciseSetEntity(exerciseEntity1.getExerciseSets().getFirst().getId(), 11, 22D,
                "nothing", 234L, now2);
        expected.setExercise(exerciseEntity1);
        exerciseSetEqualsWithKey(expected, exerciseEntity1.getExerciseSets().getFirst());
        //check results
        JPAWorkoutExerciseAdapterResult<ExerciseSet> expectedResult =
                new JPAWorkoutExerciseAdapterResult.Success<>(toDomain(exerciseEntity1.getExerciseSets().getFirst()));

        assertResultEquals(expectedResult, actualResult1, ExerciseSet::getId);
        assertResultEquals(expectedResult, actualResult2, ExerciseSet::getId);
        //check if there is one exerciseSet in the DB:
        Long count = em.createQuery("SELECT COUNT(e) FROM ExerciseSetEntity e", Long.class).getSingleResult();
        assertEquals(1, count);
    }

    @Test
    public void testEditInvalidExerciseSetInExercise() {
        em.persist(user);
        exerciseEntity1.setOwner(user);
        em.persist(exerciseEntity1);
        exerciseSet1.setBelongsToExercise(exerciseEntity1.getId());
        addExerciseSetToExercisePort.addExerciseSetToExercise(exerciseEntity1.getId(), exerciseSet1);
        ExerciseSet toBeEdited = new ExerciseSet(42671L, 2, 40D, "best", 40L,
                now1, exerciseEntity1.getId());

        //check results
        JPAWorkoutExerciseAdapterResult<ExerciseSet> actualResult1 =
                editExerciseSetPort.editExerciseSet(null, null);

        JPAWorkoutExerciseAdapterResult<ExerciseSet> actualResult2 =
                editExerciseSetPort.editExerciseSet(exerciseEntity1.getExerciseSets().getFirst().getId(), toBeEdited);

        JPAWorkoutExerciseAdapterResult<ExerciseSet> actualResult3 =
                editExerciseSetPort.editExerciseSet(42671L, toBeEdited);

        JPAWorkoutExerciseAdapterResult<ExerciseSet> expectedResult1 =
                new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);

        JPAWorkoutExerciseAdapterResult<ExerciseSet> expectedResult2 =
                new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.INVALID_REQUEST);

        JPAWorkoutExerciseAdapterResult<ExerciseSet> expectedResult3 =
                new JPAWorkoutExerciseAdapterResult.Failure<>(JPAWorkoutExerciseAdapterResult.FailureReason.EXERCISE_SET_NOT_FOUND);

        assertResultEquals(expectedResult1, actualResult1, ExerciseSet::getId);
        assertResultEquals(expectedResult2, actualResult2, ExerciseSet::getId);
        assertResultEquals(expectedResult3, actualResult3, ExerciseSet::getId);
    }
}