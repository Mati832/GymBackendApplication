package integration;

import adapter.in.services.JwtAdapter;
import application.port.out.AssignedWorkoutPorts.CreateAssignedWorkoutPort;
import application.port.out.UserPorts.SaveUserPort;
import application.port.out.WorkoutPorts.SaveWorkoutPort;
import domain.model.*;
import domain.valueobject.Gender;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class GetAssignedWorkoutsIntegrationTest {
    @Inject
    EntityManager em;
    @Inject
    SaveUserPort saveUserPort;
    @Inject
    SaveWorkoutPort saveWorkoutPort;
    @Inject
    JwtAdapter jwtService;
    @Inject
    CreateAssignedWorkoutPort createAssignedWorkoutPort;


    @AfterEach
    @Transactional
    public void tearDown() {
        em.createQuery("delete from CoachMemberEntity ").executeUpdate();
        em.createQuery("delete from AssignedWorkoutEntity ").executeUpdate();
        em.createQuery("delete from WorkoutEntity").executeUpdate();
        em.createQuery("delete from MemberEntity").executeUpdate();
        em.createQuery("delete from CoachEntity").executeUpdate();
        em.createQuery("delete from UserEntity").executeUpdate();

    }
    private void setupWorkouts(int count, Long coachId, Long memberId){

        for (int i = 0; i < count; i++) {
            Workout w = saveWorkoutPort.saveWorkout(new Workout("W"+i, "D", LocalDateTime.now(), coachId));
            createAssignedWorkoutPort.createAssignedWorkout(new AssignedWorkout(w.getId(), memberId, coachId));
        }
    }

    @Test
    public void getAssignedWorkoutsSuccessfully() {
        User coach = saveUserPort.save(new Coach("firstname", "lastname", "coach@example.com", "password", Gender.MALE, LocalDate.of(2000, 12, 1)));
        User member = saveUserPort.save(new Member("firstname", "lastname", "member@example.com", "password", Gender.MALE, LocalDate.of(2000, 12, 1)));
        String token = jwtService.generateToken(member.getId().toString());
        setupWorkouts(22, coach.getId(), member.getId());

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(io.restassured.http.ContentType.JSON)
                .when()
                .get("/users/members/" + member.getId() + "/assigned-workouts")
                .then()
                .statusCode(200);
    }
    @Test
    public void shouldReturnNextLinkWhenMoreDataExists() {
        User coach = saveUserPort.save(new Coach("firstname", "lastname", "coach@example.com", "password", Gender.MALE, LocalDate.of(2000, 12, 1)));
        User member = saveUserPort.save(new Member("firstname", "lastname", "member@example.com", "password", Gender.MALE, LocalDate.of(2000, 12, 1)));
        setupWorkouts(3,coach.getId(), member.getId());

        String token = jwtService.generateToken(member.getId().toString());

        given()
                .header("Authorization", "Bearer " + token)
                .queryParam("offset", 0)
                .queryParam("size", 2)
                .when()
                .get("/users/members/" + member.getId() + "/assigned-workouts")
                .then()
                .statusCode(200)
                .body("data.size()", is(2))
                .body("totalCount", is(3))
                .header("Link", containsString("rel=\"next\""))
                .header("Link", containsString("offset=2"));
    }

    @Test
    public void shouldReturnPrevLinkOnSecondPage() {
        User coach = saveUserPort.save(new Coach("firstname", "lastname", "coach@example.com", "password", Gender.MALE, LocalDate.of(2000, 12, 1)));
        User member = saveUserPort.save(new Member("firstname", "lastname", "member@example.com", "password", Gender.MALE, LocalDate.of(2000, 12, 1)));
        setupWorkouts(3,coach.getId(), member.getId());
        String token = jwtService.generateToken(member.getId().toString());

        given()
                .header("Authorization", "Bearer " + token)
                .queryParam("offset", 2)
                .queryParam("size", 2)
                .when()
                .get("/users/members/" + member.getId() + "/assigned-workouts")
                .then()
                .statusCode(200)
                .body("data.size()", is(1))
                .body("next", is(nullValue()))
                .header("Link", containsString("rel=\"prev\""));
    }

    @Test
    public void shouldReturnEmptyListWhenOffsetIsOutOfBounds() {
        User coach = saveUserPort.save(new Coach("firstname", "lastname", "coach@example.com", "password", Gender.MALE, LocalDate.of(2000, 12, 1)));
        User member = saveUserPort.save(new Member("firstname", "lastname", "member@example.com", "password", Gender.MALE, LocalDate.of(2000, 12, 1)));
        setupWorkouts(1,coach.getId(), member.getId());
        String token = jwtService.generateToken(member.getId().toString());

        given()
                .header("Authorization", "Bearer " + token)
                .queryParam("offset", 500)
                .when()
                .get("/users/members/" + member.getId() + "/assigned-workouts")
                .then()
                .log().all()
                .statusCode(200)
                .body("data.size()", is(0))
                .body("totalCount", is(1))
                .body("next", is(nullValue()));
    }
}

