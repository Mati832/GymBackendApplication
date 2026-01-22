package integration;

import adapter.in.DTOs.RequestDTOs.exerciseSet.ExerciseSetRequest;
import adapter.in.DTOs.ResponseDTOs.PaginatedResponseDTO;
import adapter.in.DTOs.ResponseDTOs.exerciseSets.ExerciseSetResponse;
import adapter.in.services.JwtAdapter;
import adapter.out.Entities.UserEntity;
import application.port.out.ExercisePorts.SaveExercisePort;
import application.port.out.UserPorts.SaveUserPort;
import application.port.out.WorkoutPorts.SaveWorkoutPort;
import domain.model.Exercise;
import domain.model.Member;
import domain.model.User;
import domain.model.Workout;
import domain.valueobject.Gender;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class ExerciseSetIntegrationTest {
    @Inject
    EntityManager em;
    @Inject
    SaveUserPort saveUserPort;
    @Inject
    JwtAdapter jwtAdapter;
    @Inject
    SaveWorkoutPort saveWorkoutPort;
    @Inject
    SaveExercisePort saveExercisePort;

    User user;
    User user2;
    ExerciseSetRequest exerciseSetRequest1;
    ExerciseSetRequest exerciseSetRequest2;
    LocalDateTime now1 = LocalDateTime.now();
    LocalDateTime now2 = LocalDateTime.now();

    @BeforeEach
    public void setup() {
        user = new Member("firstname", "lastname", "member@example.com", "password",
                Gender.MALE, LocalDate.of(2000, 12, 1));
        user2 = new Member("firstname", "lastname", "diffrentEmail@gmail.com", "password",
                Gender.MALE, LocalDate.of(2000, 12, 1));
        exerciseSetRequest1 = new ExerciseSetRequest(null, 10, 50.5, "nothing",60L, now1);
        exerciseSetRequest2 = new ExerciseSetRequest(null, 20, 20.25, "cool",90L, now2);
    }

    @AfterEach
    @Transactional
    public void tearDown() {
        em.createQuery("delete from ExerciseSetEntity ").executeUpdate();
        em.createQuery("delete from ExerciseEntity").executeUpdate();
        em.createQuery("delete from CoachMemberEntity").executeUpdate();
        em.createQuery("delete from AssignedWorkoutEntity").executeUpdate();
        em.createQuery("delete from WorkoutEntity").executeUpdate();

        em.createQuery("delete from MemberEntity").executeUpdate();
        em.createQuery("delete from CoachEntity").executeUpdate();

        em.createQuery("delete from UserEntity").executeUpdate();
    }

    @Test
    public void testPostExerciseSet(){
        user = saveUserPort.save(user);
        Workout workout = saveWorkoutPort.saveWorkout(new Workout(null, "cardio", "nothing",
                now1, new ArrayList<>(), user.getId()));

        Exercise exercise = saveExercisePort.saveExercise(new Exercise(null, "push-ups", "up",
                600L, user.getId(), new ArrayList<>(), now1, workout.getId()));

        //create exerciseSet with workout
        ExerciseSetResponse actual = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user))
                .body(exerciseSetRequest1)
                .when()
                .post("/users/{userId}/workouts/{workoutId}/exercises/{exerciseId}/exerciseSets", user.getId(), workout.getId(), exercise.getId())

                .then()
                .statusCode(201)
                .header("ETag", notNullValue())
                .header("Cache-Control", containsString("private"))
                .header("Location", containsString("users/" + user.getId() + "/workouts/" + workout.getId() + "/exercises/" +
                        exercise.getId() + "/exerciseSets/" + em.find(UserEntity.class, user.getId()).getWorkouts().getFirst().getExercises()
                        .getFirst().getExerciseSets().getFirst().getId()))
                .extract()
                .as(ExerciseSetResponse.class);

        ExerciseSetResponse expected = new ExerciseSetResponse( 10, 50.5, "nothing",60L, now1);
        assertEquals(expected, actual);
    }

    @Test
    public void testPostExerciseSet2(){
        user = saveUserPort.save(user);
        Exercise exercise = saveExercisePort.saveExercise(new Exercise(null, "push-ups", "up",
                600L, user.getId(), new ArrayList<>(), now1, null));

        //create exerciseSet without workout
        ExerciseSetResponse actual = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user))
                .body(exerciseSetRequest1)
                .when()
                .post("/users/{userId}/exercises/{exerciseId}/exerciseSets", user.getId(), exercise.getId())

                .then()
                .statusCode(201)
                .header("ETag", notNullValue())
                .header("Cache-Control", containsString("private"))
                .header("Location", containsString("users/" + user.getId() + "/exercises/" + exercise.getId() + "/exerciseSets/" +
                        em.find(UserEntity.class, user.getId()).getExercises().getFirst().getExerciseSets().getFirst().getId()))
                .extract()
                .as(ExerciseSetResponse.class);

        ExerciseSetResponse expected =  new ExerciseSetResponse( 10, 50.5, "nothing",60L, now1);
        assertEquals(expected, actual);
    }

    @Test
    public void testPutExerciseSet(){
        user = saveUserPort.save(user);
        Workout workout = saveWorkoutPort.saveWorkout(new Workout(null, "cardio", "nothing",
                now1, new ArrayList<>(), user.getId()));

        Exercise exercise = saveExercisePort.saveExercise(new Exercise(null, "push-ups", "up",
                600L, user.getId(), new ArrayList<>(), now1, workout.getId()));

        //create exerciseSet first
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user))
                .body(exerciseSetRequest1)
                .when()
                .post("/users/{userId}/workouts/{workoutId}/exercises/{exerciseId}/exerciseSets", user.getId(), workout.getId(), exercise.getId())

                .then()
                .header("ETag", notNullValue())
                .header("Cache-Control", containsString("private"))
                .header("Location", containsString("users/" + user.getId() + "/workouts/" + workout.getId() + "/exercises/" +
                        exercise.getId() + "/exerciseSets/" + em.find(UserEntity.class, user.getId()).getWorkouts().getFirst().getExercises()
                        .getFirst().getExerciseSets().getFirst().getId()))
                .statusCode(201);

        Long newExerciseSetId = em.find(UserEntity.class, user.getId()).getWorkouts().getFirst().getExercises()
                .getFirst().getExerciseSets().getFirst().getId();

        //change exerciseSet
        ExerciseSetResponse actual = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user))
                .body(exerciseSetRequest2)
                .when()
                .put("/users/{userId}/workouts/{workoutId}/exercises/{exerciseId}/exerciseSets/{exerciseSetId}", user.getId(),
                        workout.getId(), exercise.getId(), newExerciseSetId)

                .then()
                .statusCode(200)
                .header("ETag", notNullValue())
                .header("Cache-Control", containsString("private"))
                .extract()
                .as(ExerciseSetResponse.class);

        ExerciseSetResponse expected = new ExerciseSetResponse(20, 20.25, "cool",90L, now2);
        assertEquals(expected, actual);
    }

    @Test
    public void testPutExerciseSet2(){
        user = saveUserPort.save(user);
        Exercise exercise = saveExercisePort.saveExercise(new Exercise(null, "push-ups", "up",
                600L, user.getId(), new ArrayList<>(), now1, null));

        //create exerciseSet without workout first
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user))
                .body(exerciseSetRequest1)
                .when()
                .post("/users/{userId}/exercises/{exerciseId}/exerciseSets", user.getId(), exercise.getId())

                .then()
                .statusCode(201)
                .header("ETag", notNullValue())
                .header("Cache-Control", containsString("private"))
                .header("Location", containsString("users/" + user.getId() + "/exercises/" + exercise.getId() + "/exerciseSets/" +
                        em.find(UserEntity.class, user.getId()).getExercises().getFirst().getExerciseSets().getFirst().getId()));

        Long newExerciseSetId = em.find(UserEntity.class, user.getId()).getExercises().getFirst().getExerciseSets().getFirst().getId();

        ExerciseSetResponse actual = given()
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user))
                .contentType(ContentType.JSON)
                .body(exerciseSetRequest2)
                .when()
                .put("/users/{userId}/exercises/{exerciseId}/exerciseSets/{exerciseSetId}", user.getId(), exercise.getId(), newExerciseSetId)

                .then()
                .statusCode(200)
                .header("ETag", notNullValue())
                .header("Cache-Control", containsString("private"))
                .extract()
                .as(ExerciseSetResponse.class);

        ExerciseSetResponse expected = new ExerciseSetResponse(20, 20.25, "cool",90L, now2);
        assertEquals(expected, actual);
    }


    @Test
    public void testDeleteExerciseSet(){
        user = saveUserPort.save(user);
        Workout workout = saveWorkoutPort.saveWorkout(new Workout(null, "cardio", "nothing",
                now1, new ArrayList<>(), user.getId()));

        Exercise exercise = saveExercisePort.saveExercise(new Exercise(null, "push-ups", "up",
                600L, user.getId(), new ArrayList<>(), now1, workout.getId()));

        //create exerciseSet with workout
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user))
                .body(exerciseSetRequest1)
                .when()
                .post("/users/{userId}/workouts/{workoutId}/exercises/{exerciseId}/exerciseSets", user.getId(), workout.getId(), exercise.getId())

                .then()
                .statusCode(201)
                .header("ETag", notNullValue())
                .header("Cache-Control", containsString("private"))
                .header("Location", containsString("users/" + user.getId() + "/workouts/" + workout.getId() + "/exercises/" +
                        exercise.getId() + "/exerciseSets/" + em.find(UserEntity.class, user.getId()).getWorkouts().getFirst().getExercises()
                        .getFirst().getExerciseSets().getFirst().getId()));

        Long newExerciseSetId = em.find(UserEntity.class, user.getId()).getWorkouts().getFirst().getExercises()
                .getFirst().getExerciseSets().getFirst().getId();

        given()
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user))
                .when()
                .delete("/users/{userId}/workouts/{workoutId}/exercises/{exerciseId}/exerciseSets/{exerciseSetId}",
                        user.getId(), workout.getId(), exercise.getId(), newExerciseSetId)

                .then()
                .statusCode(204);
    }

    @Test
    public void testDeleteExerciseSet2(){
        user = saveUserPort.save(user);
        Exercise exercise = saveExercisePort.saveExercise(new Exercise(null, "push-ups", "up",
                600L, user.getId(), new ArrayList<>(), now1, null));

        //create exerciseSet without workout first
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user))
                .body(exerciseSetRequest1)
                .when()
                .post("/users/{userId}/exercises/{exerciseId}/exerciseSets", user.getId(), exercise.getId())

                .then()
                .statusCode(201)
                .header("ETag", notNullValue())
                .header("Cache-Control", containsString("private"))
                .header("Location", containsString("users/" + user.getId() + "/exercises/" + exercise.getId() + "/exerciseSets/" +
                        em.find(UserEntity.class, user.getId()).getExercises().getFirst().getExerciseSets().getFirst().getId()));

        Long newExerciseSetId = em.find(UserEntity.class, user.getId()).getExercises().getFirst().getExerciseSets().getFirst().getId();

        given()
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user))
                .when()
                .delete("/users/{userId}/exercises/{exerciseId}/exerciseSets/{exerciseSetId}", user.getId(), exercise.getId(), newExerciseSetId)

                .then()
                .statusCode(204);
    }

    @Test
    public void testGetExerciseSet(){
        user = saveUserPort.save(user);
        Exercise exercise = saveExercisePort.saveExercise(new Exercise(null, "push-ups", "up",
                600L, user.getId(), new ArrayList<>(), now1, null));

        //create exerciseSet1 without workout
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user))
                .body(exerciseSetRequest1)
                .when()
                .post("/users/{userId}/exercises/{exerciseId}/exerciseSets", user.getId(), exercise.getId())

                .then()
                .statusCode(201)
                .header("ETag", notNullValue())
                .header("Cache-Control", containsString("private"))
                .header("Location", containsString("users/" + user.getId() + "/exercises/" + exercise.getId() + "/exerciseSets/" +
                        em.find(UserEntity.class, user.getId()).getExercises().getFirst().getExerciseSets().getFirst().getId()));

        em.clear(); //empty the cache to retrieve the second exerciseSet later

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user))
                .body(exerciseSetRequest2)
                .when()
                .post("/users/{userId}/exercises/{exerciseId}/exerciseSets", user.getId(), exercise.getId())

                .then()
                .statusCode(201)
                .header("ETag", notNullValue())
                .header("Cache-Control", containsString("private"))
                .header("Location", containsString("users/" + user.getId() + "/exercises/" + exercise.getId() + "/exerciseSets/" +
                        em.find(UserEntity.class, user.getId()).getExercises().getFirst().getExerciseSets().get(1).getId()));

        //get exerciseSets with filtering (query parameters)
        PaginatedResponseDTO actual = given()
                .when()
                .get("/users/{userId}/exercises/{exerciseId}/exerciseSets?repsGreaterThan={num}", user.getId(), exercise.getId(), 15)

                .then()
                .statusCode(200)
                .header("ETag", notNullValue())
                .header("Cache-Control", containsString("private"))
                .extract()
                .as(PaginatedResponseDTO.class);

        Map<String, Object> expected = new LinkedHashMap<>();
        expected.put("reps", 20);
        expected.put("weightInKG", 20.25);
        expected.put("notes", "cool");
        expected.put("durationInSec", 90);
        expected.put("createdAt", now2.truncatedTo(ChronoUnit.SECONDS));
        Map<String, Object> actualMap = (Map<String, Object>) actual.data().getFirst();

        assertEquals(1, actual.data().size());
        assertEquals(expected.get("reps"), actualMap.get("reps"));
        assertEquals(expected.get("weightInKG"), actualMap.get("weightInKG"));
        assertEquals(expected.get("notes"), actualMap.get("notes"));
        assertEquals(expected.get("durationInSec"), actualMap.get("durationInSec"));
        assertEquals(expected.get("createdAt"),
                LocalDateTime.parse((CharSequence) actualMap.get("createdAt"), DateTimeFormatter.ISO_LOCAL_DATE_TIME).truncatedTo(ChronoUnit.SECONDS));

    }

    @Test
    public void testNegativePostExerciseSet(){
        user = saveUserPort.save(user);
        user2 = saveUserPort.save(user2);
        Workout workout = saveWorkoutPort.saveWorkout(new Workout(null, "cardio", "nothing",
                now1, new ArrayList<>(), user.getId()));

        Exercise exercise = saveExercisePort.saveExercise(new Exercise(null, "push-ups", "up",
                600L, user.getId(), new ArrayList<>(), now1, workout.getId()));

        //create exerciseSet with workout with wrong user
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user2)) //<- wrong user
                .body(exerciseSetRequest1)
                .when()
                .post("/users/{userId}/workouts/{workoutId}/exercises/{exerciseId}/exerciseSets", user.getId(), workout.getId(), exercise.getId())

                .then()
                .statusCode(403);
        //create exerciseSet with workout with unauthenticated user
        given()
                .contentType(ContentType.JSON)
                .body(exerciseSetRequest1)
                .when()
                .post("/users/{userId}/workouts/{workoutId}/exercises/{exerciseId}/exerciseSets", user.getId(), workout.getId(), exercise.getId())

                .then()
                .header("Link", containsString("rel=\"login\""))
                .statusCode(401);
    }

    @Test
    public void testNegativePostExerciseSet2(){
        user = saveUserPort.save(user);
        user2 = saveUserPort.save(user2);
        Exercise exercise = saveExercisePort.saveExercise(new Exercise(null, "push-ups", "up",
                600L, user.getId(), new ArrayList<>(), now1, null));

        //create exerciseSet without workout with wrong user
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user2)) //<- wrong user
                .body(exerciseSetRequest1)
                .when()
                .post("/users/{userId}/exercises/{exerciseId}/exerciseSets", user.getId(), exercise.getId())

                .then()
                .statusCode(403);
        //create exerciseSet without workout with unauthenticated user

        given()
                .contentType(ContentType.JSON)
                .body(exerciseSetRequest1)
                .when()
                .post("/users/{userId}/exercises/{exerciseId}/exerciseSets", user.getId(), exercise.getId())

                .then()
                .header("Link", containsString("rel=\"login\""))
                .statusCode(401);
    }

    @Test
    public void testNegativePutExerciseSet(){
        user = saveUserPort.save(user);
        user2  = saveUserPort.save(user2);
        Workout workout = saveWorkoutPort.saveWorkout(new Workout(null, "cardio", "nothing",
                now1, new ArrayList<>(), user.getId()));

        Exercise exercise = saveExercisePort.saveExercise(new Exercise(null, "push-ups", "up",
                600L, user.getId(), new ArrayList<>(), now1, workout.getId()));

        //create exerciseSet first
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user))
                .body(exerciseSetRequest1)
                .when()
                .post("/users/{userId}/workouts/{workoutId}/exercises/{exerciseId}/exerciseSets", user.getId(), workout.getId(), exercise.getId())

                .then()
                .header("ETag", notNullValue())
                .header("Cache-Control", containsString("private"))
                .header("Location", containsString("users/" + user.getId() + "/workouts/" + workout.getId() + "/exercises/" +
                        exercise.getId() + "/exerciseSets/" + em.find(UserEntity.class, user.getId()).getWorkouts().getFirst().getExercises()
                        .getFirst().getExerciseSets().getFirst().getId()))
                .statusCode(201);

        Long newExerciseSetId = em.find(UserEntity.class, user.getId()).getWorkouts().getFirst().getExercises()
                .getFirst().getExerciseSets().getFirst().getId();

        //change exerciseSet with wrong user
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user2)) //<- wrong user
                .body(exerciseSetRequest2)
                .when()
                .put("/users/{userId}/workouts/{workoutId}/exercises/{exerciseId}/exerciseSets/{exerciseSetId}", user.getId(),
                        workout.getId(), exercise.getId(), newExerciseSetId)

                .then()
                .statusCode(403);

        //change exerciseSet with unauthenticated user
        given()
                .contentType(ContentType.JSON)
                .body(exerciseSetRequest2)
                .when()
                .put("/users/{userId}/workouts/{workoutId}/exercises/{exerciseId}/exerciseSets/{exerciseSetId}", user.getId(),
                        workout.getId(), exercise.getId(), newExerciseSetId)

                .then()
                .header("Link", containsString("rel=\"login\""))
                .statusCode(401);
    }

    @Test
    public void testNegativePutExerciseSet2(){
        user = saveUserPort.save(user);
        user2  = saveUserPort.save(user2);
        Exercise exercise = saveExercisePort.saveExercise(new Exercise(null, "push-ups", "up",
                600L, user.getId(), new ArrayList<>(), now1, null));

        //create exerciseSet without workout
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user))
                .body(exerciseSetRequest1)
                .when()
                .post("/users/{userId}/exercises/{exerciseId}/exerciseSets", user.getId(), exercise.getId())

                .then()
                .statusCode(201)
                .header("ETag", notNullValue())
                .header("Cache-Control", containsString("private"))
                .header("Location", containsString("users/" + user.getId() + "/exercises/" + exercise.getId() + "/exerciseSets/" +
                        em.find(UserEntity.class, user.getId()).getExercises().getFirst().getExerciseSets().getFirst().getId()));

        //change exercise with wrong user
        Long newExerciseSetId = em.find(UserEntity.class, user.getId()).getExercises().getFirst().getExerciseSets().getFirst().getId();

        given()
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user2)) //<- wrong user
                .contentType(ContentType.JSON)
                .body(exerciseSetRequest2)
                .when()
                .put("/users/{userId}/exercises/{exerciseId}/exerciseSets/{exerciseSetId}", user.getId(), exercise.getId(), newExerciseSetId)

                .then()
                .statusCode(403);

        //change exercise with unauthenticated user
        given()
                .contentType(ContentType.JSON)
                .body(exerciseSetRequest2)
                .when()
                .put("/users/{userId}/exercises/{exerciseId}/exerciseSets/{exerciseSetId}", user.getId(), exercise.getId(), newExerciseSetId)

                .then()
                .statusCode(401)
                .header("Link",  containsString("rel=\"login\""));
    }

    @Test
    public void testNegativeDeleteExerciseSet(){
        user = saveUserPort.save(user);
        user2  = saveUserPort.save(user2);
        Workout workout = saveWorkoutPort.saveWorkout(new Workout(null, "cardio", "nothing",
                now1, new ArrayList<>(), user.getId()));

        Exercise exercise = saveExercisePort.saveExercise(new Exercise(null, "push-ups", "up",
                600L, user.getId(), new ArrayList<>(), now1, workout.getId()));

        //create exerciseSet first
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user))
                .body(exerciseSetRequest1)
                .when()
                .post("/users/{userId}/workouts/{workoutId}/exercises/{exerciseId}/exerciseSets", user.getId(), workout.getId(), exercise.getId())

                .then()
                .header("ETag", notNullValue())
                .header("Cache-Control", containsString("private"))
                .header("Location", containsString("users/" + user.getId() + "/workouts/" + workout.getId() + "/exercises/" +
                        exercise.getId() + "/exerciseSets/" + em.find(UserEntity.class, user.getId()).getWorkouts().getFirst().getExercises()
                        .getFirst().getExerciseSets().getFirst().getId()))
                .statusCode(201);


        //delete exerciseSet with workout with wrong user
        Long newExerciseSetId = em.find(UserEntity.class, user.getId()).getWorkouts().getFirst().getExercises()
                .getFirst().getExerciseSets().getFirst().getId();

        given()
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user2))//<- wrong user
                .when()
                .delete("/users/{userId}/workouts/{workoutId}/exercises/{exerciseId}/exerciseSets/{exerciseSetId}",
                        user.getId(), workout.getId(), exercise.getId(), newExerciseSetId)

                .then()
                .statusCode(403);

        //delete exerciseSet with unauthenticated user
        given()
                .when()
                .delete("/users/{userId}/workouts/{workoutId}/exercises/{exerciseId}/exerciseSets/{exerciseSetId}",
                        user.getId(), workout.getId(), exercise.getId(), newExerciseSetId)

                .then()
                .statusCode(401);
    }

    @Test
    public void testNegativeDeleteExerciseSet2(){
        user = saveUserPort.save(user);
        user2  = saveUserPort.save(user2);
        Exercise exercise = saveExercisePort.saveExercise(new Exercise(null, "push-ups", "up",
                600L, user.getId(), new ArrayList<>(), now1, null));

        //create exerciseSet without workout
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user))
                .body(exerciseSetRequest1)
                .when()
                .post("/users/{userId}/exercises/{exerciseId}/exerciseSets", user.getId(), exercise.getId())

                .then()
                .statusCode(201)
                .header("ETag", notNullValue())
                .header("Cache-Control", containsString("private"))
                .header("Location", containsString("users/" + user.getId() + "/exercises/" + exercise.getId() + "/exerciseSets/" +
                        em.find(UserEntity.class, user.getId()).getExercises().getFirst().getExerciseSets().getFirst().getId()));

        //delete exercise with wrong user
        Long newExerciseSetId = em.find(UserEntity.class, user.getId()).getExercises().getFirst().getExerciseSets().getFirst().getId();

        given()
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user2)) //<- wrong user
                .when()
                .delete("/users/{userId}/exercises/{exerciseId}/exerciseSets/{exerciseSetId}",
                        user.getId(), exercise.getId(), newExerciseSetId)

                .then()
                .statusCode(403);

        //delete exercise with unauthenticated user
        given()
                .when()
                .delete("/users/{userId}/exercises/{exerciseId}/exerciseSets/{exerciseSetId}",
                        user.getId(), exercise.getId(), newExerciseSetId)

                .then()
                .statusCode(401);
    }
}
