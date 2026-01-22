package integration;

import adapter.in.DTOs.RequestDTOs.workout.WorkoutRequest;
import adapter.in.DTOs.ResponseDTOs.PaginatedResponseDTO;
import adapter.in.DTOs.ResponseDTOs.workout.WorkoutResponse;
import adapter.in.services.JwtAdapter;
import adapter.out.Entities.UserEntity;
import application.port.out.UserPorts.SaveUserPort;
import application.port.out.WorkoutPorts.SaveWorkoutPort;
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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static adapter.in.mapper.WorkoutMapper.toResponse;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class WorkoutIntegrationTest {
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
    WorkoutRequest workoutRequest1;
    WorkoutRequest workoutRequest2;
    LocalDateTime now1 = LocalDateTime.now();
    LocalDateTime now2 = LocalDateTime.now();

    @BeforeEach
    public void setup() {
        user = new Member("firstname", "lastname", "member@example.com", "password",
                Gender.MALE, LocalDate.of(2000, 12, 1));
        user2 = new Member("firstname", "lastname", "diffrentEmail@gmail.com", "password",
                Gender.MALE, LocalDate.of(2000, 12, 1));
        workoutRequest1 = new WorkoutRequest(null, "Push", "normal", now1);
        workoutRequest2 = new WorkoutRequest(null, "Pull", "strong", now2);

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
    public void testGetWorkouts(){
        user = saveUserPort.save(user);
        PaginatedResponseDTO actual = given()
                .pathParam("userId", user.getId())
                .when()
                .get("/users/{userId}/workouts", user.getId())

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
    public void testGetWorkouts2(){
        user =  saveUserPort.save(user);
        //create 20 workouts to test pagination
        List<Object> responseData = new ArrayList<>();
        for(int i = 0; i<20; i++) {
            Workout workout = saveWorkoutPort.saveWorkout(new Workout(null, "cardio", "nothing",
                    now1, new ArrayList<>(), user.getId()));
            responseData.add(toResponse(workout));
        }
        PaginatedResponseDTO actual = given()
                .queryParam("pageSize", 5)
                .contentType(ContentType.JSON)
                .when()
                .get("/workouts?size={pageSize}", 5)

                .then()
                .statusCode(200)
                .extract()
                .as(PaginatedResponseDTO.class);

        PaginatedResponseDTO expected = new PaginatedResponseDTO(responseData, 0, 5, 4);
       // assertEquals(expected.data().getFirst(), actual.data().getFirst());
        assertEquals(expected.currentPage(), actual.currentPage());
        assertEquals(expected.pageSize(), actual.pageSize());
        assertEquals(expected.totalPages(), actual.totalPages());
    }

    @Test
    public void testPostWorkout(){
        user = saveUserPort.save(user);
        WorkoutResponse actual = given()
                .pathParam("userId", user.getId())
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user))
                .contentType("application/json")
                .body(workoutRequest1)
                .when()
                .post("/users/{userId}/workouts", user.getId())

                .then()
                .statusCode(201)
                .header("ETag", notNullValue())
                .header("Cache-Control", containsString("private"))
                .header("Location", containsString("users/"+user.getId()+"/workouts/" + em.find(UserEntity.class, user.getId()).getWorkouts().getFirst().getId()))
                .extract()
                .as(WorkoutResponse.class);

        WorkoutResponse expected = new WorkoutResponse("Push", "normal", now1);
        assertEquals(expected, actual);
    }

    @Test
    public void testPutWorkout(){
        user = saveUserPort.save(user);
        //create workout in user
        given()
                .pathParam("userId", user.getId())
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user))
                .contentType("application/json")
                .body(workoutRequest1)
                .when()
                .post("/users/{userId}/workouts", user.getId());

        Long newWorkoutId = em.find(UserEntity.class, user.getId()).getWorkouts().getFirst().getId();

        //change workout
        WorkoutResponse actual = given()
                .pathParam("userId", user.getId())
                .pathParam("workoutId", newWorkoutId)
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user))
                .contentType("application/json")
                .body(new WorkoutRequest(newWorkoutId, "Pull", "strong", now2))
                .when()
                .put("/users/{userId}/workouts/{workoutId}", user.getId(), newWorkoutId)

                .then()
                .statusCode(200)
                .header("ETag", notNullValue())
                .header("Cache-Control", containsString("private"))
                .extract()
                .as(WorkoutResponse.class);

        WorkoutResponse expected = new  WorkoutResponse("Pull", "strong", now2);
        assertEquals(expected, actual);
    }

    @Test
    public void testGetOneWorkout(){
        user = saveUserPort.save(user);
        //create a workout first
        given()
                .pathParam("userId", user.getId())
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user))
                .contentType("application/json")
                .body(workoutRequest1)
                .when()
                .post("/users/{userId}/workouts", user.getId());

        Long newWorkoutId = em.find(UserEntity.class, user.getId()).getWorkouts().getFirst().getId();

        WorkoutResponse actual =
                given()
                        .pathParam("userId", user.getId())
                        .pathParam("workoutId", newWorkoutId)
                        .when()
                        .get("/users/{userId}/workouts/{workoutId}")

                        .then()
                        .statusCode(200)
                        .header("ETag", notNullValue())
                        .header("Cache-Control", containsString("private"))
                        .extract()
                        .as(WorkoutResponse.class);

        WorkoutResponse expected = new WorkoutResponse("Push", "normal", now1);
        assertEquals(expected.name(), actual.name());
        assertEquals(expected.description(), actual.description());
        assertEquals(expected.createdAt().truncatedTo(ChronoUnit.MILLIS), actual.createdAt().truncatedTo(ChronoUnit.MILLIS));
    }

    @Test
    public void testDeleteWorkout(){
        user = saveUserPort.save(user);

        //create a workout first
        given()
                .pathParam("userId", user.getId())
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user))
                .contentType("application/json")
                .body(workoutRequest1)
                .when()
                .post("/users/{userId}/workouts", user.getId());

        Long newWorkoutId = em.find(UserEntity.class, user.getId()).getWorkouts().getFirst().getId();

        given()
                .pathParam("userId", user.getId())
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user))
                .pathParam("workoutId", newWorkoutId)
                .when()
                .delete("/users/{userId}/workouts/{workoutId}")

                .then()
                .statusCode(204);
    }

    @Test
    public void testNegativePostWorkout(){
        user = saveUserPort.save(user);

        //create workout with wrong user:
        user2 = saveUserPort.save(user2);
        given()
                .pathParam("userId", user.getId())
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user2))
                .contentType("application/json")
                .body(workoutRequest1)
                .when()
                .post("/users/{userId}/workouts", user.getId())

                .then()
                .statusCode(403);
        //creat workout with unauthenticated user
        given()
                .pathParam("userId", user.getId()) //no Authorization header
                .contentType("application/json")
                .body(workoutRequest1)
                .when()
                .post("/users/{userId}/workouts", user.getId())

                .then()
                .statusCode(401);
    }

    @Test
    public void testNegativeDeleteWorkout(){
        user = saveUserPort.save(user);
        user2 = saveUserPort.save(user2);

        //create a workout first
        given()
                .pathParam("userId", user.getId())
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user))
                .contentType("application/json")
                .body(workoutRequest1)
                .when()
                .post("/users/{userId}/workouts", user.getId());

        Long newWorkoutId = em.find(UserEntity.class, user.getId()).getWorkouts().getFirst().getId();

        //wrong user
        given()
                .pathParam("userId", user.getId())
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user2))
                .pathParam("workoutId", newWorkoutId)
                .when()
                .delete("/users/{userId}/workouts/{workoutId}")

                .then()
                .statusCode(403);

        //unauthenticated user
        given()
                .pathParam("userId", user.getId())
                .pathParam("workoutId", newWorkoutId)
                .when()
                .delete("/users/{userId}/workouts/{workoutId}")

                .then()
                .statusCode(401);
    }

    @Test
    public void testNegativePutWorkout(){
        user = saveUserPort.save(user);
        user2 = saveUserPort.save(user2);

        //create a workout first
        given()
                .pathParam("userId", user.getId())
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user))
                .contentType("application/json")
                .body(workoutRequest1)
                .when()
                .post("/users/{userId}/workouts", user.getId());

        Long newWorkoutId = em.find(UserEntity.class, user.getId()).getWorkouts().getFirst().getId();

        //wrong user
        given()
                .pathParam("userId", user.getId())
                .pathParam("workoutId", newWorkoutId)
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user2))
                .contentType("application/json")
                .body(workoutRequest2)
                .when()
                .put("/users/{userId}/workouts/{workoutId}")

                .then()
                .statusCode(403);

        //unauthenticated user
        given()
                .pathParam("userId", user.getId())
                .pathParam("workoutId", newWorkoutId)
                .contentType("application/json")
                .body(workoutRequest2)
                .when()
                .put("/users/{userId}/workouts/{workoutId}")

                .then()
                .statusCode(401);
    }

    @Test
    public void testNegativeGetWorkouts(){

        given()
                .pathParam("userId", 38723L)
                .when()
                .get("/users/{userId}/workouts", 38723L)

                .then()
                .statusCode(404);
    }

    @Test
    public void testNegativeGetOneWorkout(){
        user = saveUserPort.save(user);
        //create a workout first
        given()
                .pathParam("userId", user.getId())
                .header("Authorization", "Bearer " + jwtAdapter.generateToken(user))
                .contentType("application/json")
                .body(workoutRequest1)
                .when()
                .post("/users/{userId}/workouts", user.getId());


        //give wrong id
        given()
                .pathParam("userId", user.getId())
                .pathParam("workoutId", 397862L) //wrong ID
                .when()
                .get("/users/{userId}/workouts/{workoutId}")
                .then()
                .statusCode(404);
    }
}