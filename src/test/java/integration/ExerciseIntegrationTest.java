package integration;

import adapter.in.DTOs.RequestDTOs.exercise.ExerciseRequest;
import adapter.in.DTOs.ResponseDTOs.PaginatedResponseDTO;
import adapter.in.DTOs.ResponseDTOs.exercises.ExerciseResponse;
import adapter.in.services.JwtAdapter;
import adapter.out.Entities.UserEntity;
import application.port.out.UserPorts.SaveUserPort;
import application.port.out.WorkoutPorts.SaveWorkoutPort;
import domain.model.Member;
import domain.model.User;
import domain.model.Workout;
import domain.valueobject.Gender;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class ExerciseIntegrationTest {
    @Inject
    EntityManager em;
    @Inject
    SaveUserPort saveUserPort;
    @Inject
    JwtAdapter jwtAdapter;
    @Inject
    SaveWorkoutPort saveWorkoutPort;

    User user;
    User user2;
    ExerciseRequest exerciseRequest1;
    ExerciseRequest exerciseRequest2;
    LocalDateTime now1 = LocalDateTime.now();
    LocalDateTime now2 = LocalDateTime.now();

    @BeforeEach
    public void setup() {
        user = new Member("firstname", "lastname", "member@example.com", "password",
                Gender.MALE, LocalDate.of(2000, 12, 1));
        user2 = new Member("firstname", "lastname", "diffrentEmail@gmail.com", "password",
                Gender.MALE, LocalDate.of(2000, 12, 1));
        exerciseRequest1 = new ExerciseRequest(null, "pull-ups", "upper body", 600L, now1);
        exerciseRequest2 = new ExerciseRequest(null, "push-ups", "up", 900L, now2);

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
    public void testGetExercises(){
        user = saveUserPort.save(user);
        PaginatedResponseDTO actual = given()
                .pathParam("userId", user.getId())
                .when()
                .get("/users/{userId}/exercises", user.getId())

                .then()
                .statusCode(200)
                .header("ETag", notNullValue())
                .header("Cache-Control", containsString("private"))
                .extract()
                .as(PaginatedResponseDTO.class);

        PaginatedResponseDTO expected = new PaginatedResponseDTO(new ArrayList<>(), 0, 10, 0);
        assertEquals(expected, actual);
    }

    @Test
    public void testPostExercises(){
        //create exercise in user Repo:
        user = saveUserPort.save(user);
        ExerciseResponse actual = given()
                .pathParam("userId", user.getId())
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user))
                .contentType("application/json")
                .body(exerciseRequest1)
                .when()
                .post("/users/{userId}/exercises", user.getId())

                .then()
                .statusCode(201)
                .header("ETag", notNullValue())
                .header("Cache-Control", containsString("private"))
                .header("Location", containsString("users/"+user.getId()+"/exercises/" + em.find(UserEntity.class, user.getId()).getExercises().getFirst().getId()))
                .extract()
                .as(ExerciseResponse.class);
        ExerciseResponse expected = new ExerciseResponse("pull-ups", "upper body", 600L, now1);
        assertEquals(expected, actual);
    }

    @Test
    public void testPostExercises2(){
        //create exercise in workout
        user = saveUserPort.save(user);
        Workout workout = saveWorkoutPort.saveWorkout(new Workout(null, "cardio", "nothing",
                now1, new ArrayList<>(), user.getId()));

        ExerciseResponse actual = given()
                .pathParam("userId", user.getId())
                .pathParam("workoutId", workout.getId())
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user))
                .contentType("application/json")
                .body(exerciseRequest1)
                .when()
                .post("/users/{userId}/workouts/{workoutId}/exercises", user.getId(), workout.getId())

                .then()
                .statusCode(201)
                .header("ETag", notNullValue())
                .header("Cache-Control", containsString("private"))
                .header("Location", containsString("users/"+user.getId()+"/workouts/" + workout.getId() + "/exercises/" +
                        em.find(UserEntity.class, user.getId()).getWorkouts().getFirst().getExercises().getFirst().getId()))
                .extract()
                .as(ExerciseResponse.class);
        ExerciseResponse expected = new ExerciseResponse("pull-ups", "upper body", 600L, now1);
        assertEquals(expected, actual);
    }

    @Test
    public void testPutExercise(){
        user = saveUserPort.save(user);
        //create exercise in user Repo:
        given()
                .pathParam("userId", user.getId())
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user))
                .contentType("application/json")
                .body(exerciseRequest1)
                .when()
                .post("/users/{userId}/exercises", user.getId())

                .then()
                .statusCode(201)
                .header("ETag", notNullValue())
                .header("Cache-Control", containsString("private"))
                .header("Location", containsString("users/"+user.getId()+"/exercises/" +
                        em.find(UserEntity.class, user.getId()).getExercises().getFirst().getId()));

        ExerciseResponse actual =  given()
                .pathParam("userId", user.getId())
                .pathParam("exerciseId", em.find(UserEntity.class, user.getId()).getExercises().getFirst().getId())
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user))
                .contentType("application/json")
                .body(exerciseRequest2)
                .when()
                .put("/users/{userId}/exercises/{exerciseId}", user.getId(), em.find(UserEntity.class, user.getId()).getExercises().getFirst().getId())

                .then()
                .statusCode(200)
                .header("ETag", notNullValue())
                .header("Cache-Control", containsString("private"))
                .extract()
                .as(ExerciseResponse.class);

        ExerciseResponse expected = new ExerciseResponse("push-ups", "up", 900L, now2);
        assertEquals(expected.name(), actual.name());
        assertEquals(expected.type(), actual.type());
        assertEquals(expected.durationInSec(), actual.durationInSec());
        assertEquals(expected.createdAt().truncatedTo(ChronoUnit.SECONDS), actual.createdAt().truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    public void testPutExercise2(){
        //create exercise in workout
        user = saveUserPort.save(user);
        Workout workout = saveWorkoutPort.saveWorkout(new Workout(null, "cardio", "nothing",
                now1, new ArrayList<>(), user.getId()));

        given()
                .pathParam("userId", user.getId())
                .pathParam("workoutId", workout.getId())
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user))
                .contentType("application/json")
                .body(exerciseRequest1)
                .when()
                .post("/users/{userId}/workouts/{workoutId}/exercises", user.getId(), workout.getId())

                .then()
                .statusCode(201)
                .header("ETag", notNullValue())
                .header("Cache-Control", containsString("private"))
                .header("Location", containsString("users/"+user.getId()+"/workouts/" + workout.getId() + "/exercises/" +
                        em.find(UserEntity.class, user.getId()).getWorkouts().getFirst().getExercises().getFirst().getId()));

        ExerciseResponse actual =  given()
                .pathParam("userId", user.getId())
                .pathParam("workoutId", workout.getId())
                .pathParam("exerciseId", em.find(UserEntity.class, user.getId()).getExercises().getFirst().getId())
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user))
                .contentType("application/json")
                .body(exerciseRequest2)
                .when()
                .put("/users/{userId}/workouts/{workoutId}/exercises/{exerciseId}", user.getId(), workout.getId(),
                        em.find(UserEntity.class, user.getId()).getExercises().getFirst().getId())

                .then()
                .statusCode(200)
                .header("ETag", notNullValue())
                .header("Cache-Control", containsString("private"))
                .extract()
                .as(ExerciseResponse.class);

        ExerciseResponse expected = new ExerciseResponse("push-ups", "up", 900L, now2);
        assertEquals(expected.name(), actual.name());
        assertEquals(expected.type(), actual.type());
        assertEquals(expected.durationInSec(), actual.durationInSec());
        assertEquals(expected.createdAt().truncatedTo(ChronoUnit.SECONDS), actual.createdAt().truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    public void testGetOneExercise(){
        //get one exercise in user
        //create exercise in user Repo:
        user = saveUserPort.save(user);
        given()
                .pathParam("userId", user.getId())
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user))
                .contentType("application/json")
                .body(exerciseRequest1)
                .when()
                .post("/users/{userId}/exercises", user.getId())

                .then()
                .statusCode(201)
                .header("ETag", notNullValue())
                .header("Cache-Control", containsString("private"))
                .header("Location", containsString("users/"+user.getId()+"/exercises/" + em.find(UserEntity.class, user.getId()).getExercises().getFirst().getId()));

        //get exercise in user
        Long newExerciseId = em.find(UserEntity.class, user.getId()).getExercises().getFirst().getId();

        ExerciseResponse actual = given()
                .pathParam("userId", user.getId())
                .pathParam("exerciseId", newExerciseId)
                .when()
                .get("users/{userId}/exercises/{exerciseId}", user.getId(), newExerciseId)

                .then()
                .statusCode(200)
                .header("ETag", notNullValue())
                .header("Cache-Control", containsString("private"))
                .extract()
                .as(ExerciseResponse.class);

        ExerciseResponse expected = new ExerciseResponse("pull-ups", "upper body", 600L, now1);
        assertEquals(expected.name(), actual.name());
        assertEquals(expected.type(), actual.type());
        assertEquals(expected.durationInSec(), actual.durationInSec());
        assertEquals(expected.createdAt().truncatedTo(ChronoUnit.SECONDS), actual.createdAt().truncatedTo(ChronoUnit.SECONDS));
    }


    @Test
    public void testGetOneExercise2(){
        //get one exercise in workout
        user = saveUserPort.save(user);
        Workout workout = saveWorkoutPort.saveWorkout(new Workout(null, "cardio", "nothing",
                now1, new ArrayList<>(), user.getId()));
        //create exercise in workout
        given()
                .pathParam("userId", user.getId())
                .pathParam("workoutId", workout.getId())
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user))
                .contentType("application/json")
                .body(exerciseRequest1)
                .when()
                .post("/users/{userId}/workouts/{workoutId}/exercises", user.getId(), workout.getId())

                .then()
                .statusCode(201)
                .header("ETag", notNullValue())
                .header("Cache-Control", containsString("private"))
                .header("Location", containsString("users/"+user.getId()+"/workouts/" + workout.getId() + "/exercises/" +
                        em.find(UserEntity.class, user.getId()).getWorkouts().getFirst().getExercises().getFirst().getId()));

        Long newExerciseId = em.find(UserEntity.class, user.getId()).getExercises().getFirst().getId();

        ExerciseResponse actual = given()
                .pathParam("userId", user.getId())
                .pathParam("exerciseId", newExerciseId)
                .when()
                .get("users/{userId}/workouts/{workoutId}/exercises/{exerciseId}", user.getId(), workout.getId(), newExerciseId)

                .then()
                .statusCode(200)
                .header("ETag", notNullValue())
                .header("Cache-Control", containsString("private"))
                .extract()
                .as(ExerciseResponse.class);

        ExerciseResponse expected = new ExerciseResponse("pull-ups", "upper body", 600L, now1);
        assertEquals(expected.name(), actual.name());
        assertEquals(expected.type(), actual.type());
        assertEquals(expected.durationInSec(), actual.durationInSec());
        assertEquals(expected.createdAt().truncatedTo(ChronoUnit.SECONDS), actual.createdAt().truncatedTo(ChronoUnit.SECONDS));
    }


    @Test
    public void testDeleteExercise(){
        //create exercise in user Repo:
        user = saveUserPort.save(user);
        given()
                .pathParam("userId", user.getId())
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user))
                .contentType("application/json")
                .body(exerciseRequest1)
                .when()
                .post("/users/{userId}/exercises", user.getId())

                .then()
                .statusCode(201)
                .header("ETag", notNullValue())
                .header("Cache-Control", containsString("private"))
                .header("Location", containsString("users/"+user.getId()+"/exercises/" + em.find(UserEntity.class, user.getId()).getExercises().getFirst().getId()));

        Long newExerciseId = em.find(UserEntity.class, user.getId()).getExercises().getFirst().getId();
        given()
                .pathParam("userId", user.getId())
                .pathParam("exerciseId", newExerciseId)
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user))
                .when()
                .delete("/users/{userId}/exercises/{exerciseId}", user.getId(), newExerciseId)

                .then()
                .statusCode(204);

    }

    @Test
    public void testDeleteExercise2(){
        user = saveUserPort.save(user);
        Workout workout = saveWorkoutPort.saveWorkout(new Workout(null, "cardio", "nothing",
                now1, new ArrayList<>(), user.getId()));
        //create exercise in workout
        given()
                .pathParam("userId", user.getId())
                .pathParam("workoutId", workout.getId())
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user))
                .contentType("application/json")
                .body(exerciseRequest1)
                .when()
                .post("/users/{userId}/workouts/{workoutId}/exercises", user.getId(), workout.getId())

                .then()
                .statusCode(201)
                .header("ETag", notNullValue())
                .header("Cache-Control", containsString("private"))
                .header("Location", containsString("users/"+user.getId()+"/workouts/" + workout.getId() + "/exercises/" +
                        em.find(UserEntity.class, user.getId()).getWorkouts().getFirst().getExercises().getFirst().getId()));

        Long newExerciseId = em.find(UserEntity.class, user.getId()).getWorkouts().getFirst().getExercises().getFirst().getId();

        given()
                .pathParam("userId", user.getId())
                .pathParam("workoutId", workout.getId())
                .pathParam("exerciseId", newExerciseId)
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user))
                .when()
                .delete("/users/{userId}/workouts/{workoutId}/exercises/{exerciseId}", user.getId(), workout.getId(), newExerciseId)

                .then()
                .statusCode(204);
    }

    @Test
    public void testNegativePostExercise(){
        user = saveUserPort.save(user);
        Workout workout = saveWorkoutPort.saveWorkout(new Workout(null, "cardio", "nothing",
                now1, new ArrayList<>(), user.getId()));

        //creat exercise with wrong user
        user2 = saveUserPort.save(user2);
        given()
                .pathParam("userId", user.getId())
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user2))
                .contentType("application/json")
                .body(exerciseRequest1)
                .when()
                .post("/users/{userId}/exercises", user.getId())

                .then()
                .statusCode(403);
        //with unauthorized user
        given()
                .pathParam("userId", user.getId())
                .contentType("application/json")
                .body(exerciseRequest1)
                .when()
                .post("/users/{userId}/exercises", user.getId())

                .then()
                .statusCode(401);

        //create exercise in workout with wrong user
        given()
                .pathParam("userId", user.getId())
                .pathParam("workoutId", workout.getId())
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user2)) //<- wrong user
                .contentType("application/json")
                .body(exerciseRequest1)
                .when()
                .post("/users/{userId}/workouts/{workoutId}/exercises", user.getId(), workout.getId())

                .then()
                .statusCode(403);
        //with unauthorized user
        given()
                .pathParam("userId", user.getId())
                .pathParam("workoutId", workout.getId())
                .contentType("application/json")
                .body(exerciseRequest1)
                .when()
                .post("/users/{userId}/workouts/{workoutId}/exercises", user.getId(), workout.getId())

                .then()
                .statusCode(401);
    }

    @Test
    public void testNegativeDeleteExercise(){
        user = saveUserPort.save(user);
        Workout workout = saveWorkoutPort.saveWorkout(new Workout(null, "cardio", "nothing",
                now1, new ArrayList<>(), user.getId()));
        user2 = saveUserPort.save(user2);
        //create exercise in user repo
        given()
                .pathParam("userId", user.getId())
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user))
                .contentType("application/json")
                .body(exerciseRequest1)
                .when()
                .post("/users/{userId}/exercises", user.getId())

                .then()
                .statusCode(201)
                .header("ETag", notNullValue())
                .header("Cache-Control", containsString("private"))
                .header("Location", containsString("users/"+user.getId()+"/exercises/" + em.find(UserEntity.class, user.getId()).getExercises().getFirst().getId()));

        //delete with wrong user
        Long newExerciseId = em.find(UserEntity.class, user.getId()).getExercises().getFirst().getId();
        given()
                .pathParam("userId", user.getId())
                .pathParam("exerciseId", newExerciseId)
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user2)) //<-wrong user
                .when()
                .delete("/users/{userId}/exercises/{exerciseId}", user.getId(), newExerciseId)

                .then()
                .statusCode(403);

        //delete with unauthenticated user
        given()
                .pathParam("userId", user.getId())
                .pathParam("exerciseId", newExerciseId)
                .when()
                .delete("/users/{userId}/exercises/{exerciseId}", user.getId(), newExerciseId)

                .then()
                .statusCode(401);

        //create exercise IN workout
        given()
                .pathParam("userId", user.getId())
                .pathParam("workoutId", workout.getId())
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user))
                .contentType("application/json")
                .body(exerciseRequest1)
                .when()
                .post("/users/{userId}/workouts/{workoutId}/exercises", user.getId(), workout.getId())

                .then()
                .statusCode(201)
                .header("ETag", notNullValue())
                .header("Cache-Control", containsString("private"))
                .header("Location", containsString("users/"+user.getId()+"/workouts/" + workout.getId() + "/exercises/" +
                        em.find(UserEntity.class, user.getId()).getWorkouts().getFirst().getExercises().getFirst().getId()));

        newExerciseId = em.find(UserEntity.class, user.getId()).getWorkouts().getFirst().getExercises().getFirst().getId();

        //delete with wrong user
        given()
                .pathParam("userId", user.getId())
                .pathParam("workoutId", workout.getId())
                .pathParam("exerciseId", newExerciseId)
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user2)) //<- wrong user
                .when()
                .delete("/users/{userId}/workouts/{workoutId}/exercises/{exerciseId}", user.getId(), workout.getId(), newExerciseId)

                .then()
                .statusCode(403);

        //delete with unauthenticated user
        given()
                .pathParam("userId", user.getId())
                .pathParam("workoutId", workout.getId())
                .pathParam("exerciseId", newExerciseId)
                .when()
                .delete("/users/{userId}/workouts/{workoutId}/exercises/{exerciseId}", user.getId(), workout.getId(), newExerciseId)

                .then()
                .statusCode(401);
    }

    @Test
    public void testNegativePutExercise(){
        user = saveUserPort.save(user);
        Workout workout = saveWorkoutPort.saveWorkout(new Workout(null, "cardio", "nothing",
                now1, new ArrayList<>(), user.getId()));
        user2 = saveUserPort.save(user2);

        //create Exercise in user repo
        given()
                .pathParam("userId", user.getId())
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user))
                .contentType("application/json")
                .body(exerciseRequest1)
                .when()
                .post("/users/{userId}/exercises", user.getId())

                .then()
                .statusCode(201)
                .header("ETag", notNullValue())
                .header("Cache-Control", containsString("private"))
                .header("Location", containsString("users/"+user.getId()+"/exercises/" + em.find(UserEntity.class, user.getId()).getExercises().getFirst().getId()));

        //change exercise with wrong user
        given()
                .pathParam("userId", user.getId())
                .pathParam("exerciseId", em.find(UserEntity.class, user.getId()).getExercises().getFirst().getId())
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user2)) //<- wrong user
                .contentType("application/json")
                .body(exerciseRequest2)
                .when()
                .put("/users/{userId}/exercises/{exerciseId}", user.getId(), em.find(UserEntity.class, user.getId()).getExercises().getFirst().getId())

                .then()
                .statusCode(403);

        //change exercise with unauthenticated user
        given()
                .pathParam("userId", user.getId())
                .pathParam("exerciseId", em.find(UserEntity.class, user.getId()).getExercises().getFirst().getId())
                .contentType("application/json")
                .body(exerciseRequest2)
                .when()
                .put("/users/{userId}/exercises/{exerciseId}", user.getId(), em.find(UserEntity.class, user.getId()).getExercises().getFirst().getId())

                .then()
                .statusCode(401);

        //create Exercise in workout
        given()
                .pathParam("userId", user.getId())
                .pathParam("workoutId", workout.getId())
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user))
                .contentType("application/json")
                .body(exerciseRequest1)
                .when()
                .post("/users/{userId}/workouts/{workoutId}/exercises", user.getId(), workout.getId())

                .then()
                .statusCode(201)
                .header("ETag", notNullValue())
                .header("Cache-Control", containsString("private"))
                .header("Location", containsString("users/"+user.getId()+"/workouts/" + workout.getId() + "/exercises/" +
                        em.find(UserEntity.class, user.getId()).getWorkouts().getFirst().getExercises().getFirst().getId()));

        //change exercise in workout with wrong user
        given()
                .pathParam("userId", user.getId())
                .pathParam("workoutId", workout.getId())
                .pathParam("exerciseId", em.find(UserEntity.class, user.getId()).getWorkouts().getFirst().getExercises().getFirst().getId())
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user2)) //<- wrong user
                .contentType("application/json")
                .body(exerciseRequest2)
                .when()
                .put("/users/{userId}/workouts/{workoutId}/exercises/{exerciseId}", user.getId(), workout.getId(),
                        em.find(UserEntity.class, user.getId()).getWorkouts().getFirst().getExercises().getFirst().getId())

                .then()
                .statusCode(403);

        //change exercise int workout with unauthenticated user
        given()
                .pathParam("userId", user.getId())
                .pathParam("workoutId", workout.getId())
                .pathParam("exerciseId", em.find(UserEntity.class, user.getId()).getWorkouts().getFirst().getExercises().getFirst().getId())
                .contentType("application/json")
                .body(exerciseRequest2)
                .when()
                .put("/users/{userId}/workouts/{workoutId}/exercises/{exerciseId}", user.getId(), workout.getId(),
                        em.find(UserEntity.class, user.getId()).getWorkouts().getFirst().getExercises().getFirst().getId())

                .then()
                .statusCode(401);
    }

    @Test
    public void testNegativeGetWorkout(){
        user = saveUserPort.save(user);

        //create Exercise
        given()
                .pathParam("userId", user.getId())
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user))
                .contentType("application/json")
                .body(exerciseRequest1)
                .when()
                .post("/users/{userId}/exercises", user.getId())

                .then()
                .statusCode(201)
                .header("ETag", notNullValue())
                .header("Cache-Control", containsString("private"))
                .header("Location", containsString("users/"+user.getId()+"/exercises/" + em.find(UserEntity.class, user.getId()).getExercises().getFirst().getId()));

        given()
                .pathParam("userId", user.getId())
                .when()
                .get("/users/{userId}/exercises/{exerciseId}", 98761L,  em.find(UserEntity.class, user.getId()).getExercises().getFirst().getId())

                .then()
                .statusCode(404);

        given()
                .pathParam("userId", user.getId())
                .when()
                .get("/users/{userId}/exercises/{exerciseId}", user.getId(),  39401L)

                .then()
                .statusCode(404);
    }
}