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
import static org.hamcrest.MatcherAssert.assertThat;
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

    private void setupWorkouts(int count, Long coachId, Long memberId) {

        for (int i = 0; i < count; i++) {
            Workout w = saveWorkoutPort.saveWorkout(new Workout("W" + i, "D", LocalDateTime.now(), coachId));
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
                .log().all()
                .statusCode(200);
    }

    @Test
    public void shouldReturnNextLinkWhenMoreDataExists() {
        User coach = saveUserPort.save(new Coach("firstname", "lastname", "coach@example.com", "password", Gender.MALE, LocalDate.of(2000, 12, 1)));
        User member = saveUserPort.save(new Member("firstname", "lastname", "member@example.com", "password", Gender.MALE, LocalDate.of(2000, 12, 1)));
        setupWorkouts(3, coach.getId(), member.getId());

        String token = jwtService.generateToken(member.getId().toString());

        var response = given()
                .header("Authorization", "Bearer " + token)
                .queryParam("offset", 0)
                .queryParam("size", 2)
                .when()
                .get("/users/members/" + member.getId() + "/assigned-workouts")
                .then()
                .statusCode(200)
                .body("data.size()", is(2))
                .body("totalCount", is(3))
                .extract();

        assertThat(response.headers().getValues("Link"), hasItem(containsString("rel=\"next\"")));
        assertThat(response.headers().getValues("Link"), hasItem(containsString("rel=\"self\"")));
        assertThat(response.headers().getValues("Link"), hasItem(containsString("rel=\"filter\"")));
    }

    @Test
    public void shouldReturnPrevLinkOnSecondPage() {
        User coach = saveUserPort.save(new Coach("firstname", "lastname", "coach@example.com", "password", Gender.MALE, LocalDate.of(2000, 12, 1)));
        User member = saveUserPort.save(new Member("firstname", "lastname", "member@example.com", "password", Gender.MALE, LocalDate.of(2000, 12, 1)));
        setupWorkouts(3, coach.getId(), member.getId());
        String token = jwtService.generateToken(member.getId().toString());

        var response = given()
                .header("Authorization", "Bearer " + token)
                .queryParam("offset", 2)
                .queryParam("size", 2)
                .when()
                .get("/users/members/" + member.getId() + "/assigned-workouts")
                .then()
                .statusCode(200)
                .body("data.size()", is(1))
                .body("next", is(nullValue()))
                .extract();

        assertThat(response.headers().getValues("Link"), hasItem(containsString("rel=\"prev\"")));

    }

    @Test
    public void shouldReturnEmptyListWhenOffsetIsOutOfBounds() {
        User coach = saveUserPort.save(new Coach("firstname", "lastname", "coach@example.com", "password", Gender.MALE, LocalDate.of(2000, 12, 1)));
        User member = saveUserPort.save(new Member("firstname", "lastname", "member@example.com", "password", Gender.MALE, LocalDate.of(2000, 12, 1)));
        setupWorkouts(1, coach.getId(), member.getId());
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

    @Test
    public void shouldFilterByCoachId() {

        User coach1 = saveUserPort.save(new Coach("Coach", "One", "c1@example.com", "pw", Gender.MALE, LocalDate.of(2000, 1, 1)));
        User coach2 = saveUserPort.save(new Coach("Coach", "Two", "c2@example.com", "pw", Gender.MALE, LocalDate.of(2000, 1, 1)));
        User member = saveUserPort.save(new Member("Member", "One", "m1@example.com", "pw", Gender.MALE, LocalDate.of(2000, 1, 1)));
        String token = jwtService.generateToken(member.getId().toString());


        Workout w1 = saveWorkoutPort.saveWorkout(new Workout("Workout 1", "Desc", LocalDateTime.now(), coach1.getId()));
        createAssignedWorkoutPort.createAssignedWorkout(new AssignedWorkout(w1.getId(), member.getId(), coach1.getId()));

        Workout w2 = saveWorkoutPort.saveWorkout(new Workout("Workout 2", "Desc", LocalDateTime.now(), coach2.getId()));
        createAssignedWorkoutPort.createAssignedWorkout(new AssignedWorkout(w2.getId(), member.getId(), coach2.getId()));


        given()
                .header("Authorization", "Bearer " + token)
                .queryParam("coachId", coach1.getId())
                .when()
                .get("/users/members/" + member.getId() + "/assigned-workouts")
                .then()
                .statusCode(200)
                .body("data.size()", is(1))
                .body("totalCount", is(1))
                .body("data[0].coachLink", containsString(coach1.getId().toString()));
    }

    @Test
    public void shouldFilterBySearchStringInWorkoutName() {
        User coach = saveUserPort.save(new Coach("C", "L", "c@ex.com", "pw", Gender.MALE, LocalDate.of(2000, 1, 1)));
        User member = saveUserPort.save(new Member("M", "L", "m@ex.com", "pw", Gender.MALE, LocalDate.of(2000, 1, 1)));
        String token = jwtService.generateToken(member.getId().toString());


        Workout w1 = saveWorkoutPort.saveWorkout(new Workout("Rückentraining", "Fokus unterer Rücken", LocalDateTime.now(), coach.getId()));
        createAssignedWorkoutPort.createAssignedWorkout(new AssignedWorkout(w1.getId(), member.getId(), coach.getId()));

        Workout w2 = saveWorkoutPort.saveWorkout(new Workout("Beintraining", "Kniebeugen", LocalDateTime.now(), coach.getId()));
        createAssignedWorkoutPort.createAssignedWorkout(new AssignedWorkout(w2.getId(), member.getId(), coach.getId()));

        given()
                .header("Authorization", "Bearer " + token)
                .queryParam("search", "rücken")
                .when()
                .get("/users/members/" + member.getId() + "/assigned-workouts")
                .then()
                .statusCode(200)
                .body("data.size()", is(1))
                .body("totalCount", is(1));
    }

    @Test
    public void shouldFilterBySearchStringInCoachLastName() {

        User coachMueller = saveUserPort.save(new Coach("Thomas", "Mueller", "mueller@ex.com", "pw", Gender.MALE, LocalDate.of(1990, 1, 1)));
        User coachSchmidt = saveUserPort.save(new Coach("Kevin", "Schmidt", "schmidt@ex.com", "pw", Gender.MALE, LocalDate.of(1990, 1, 1)));
        User member = saveUserPort.save(new Member("M", "L", "m@ex.com", "pw", Gender.MALE, LocalDate.of(2000, 1, 1)));
        String token = jwtService.generateToken(member.getId().toString());

        Workout w1 = saveWorkoutPort.saveWorkout(new Workout("Plan 1", "D", LocalDateTime.now(), coachMueller.getId()));
        createAssignedWorkoutPort.createAssignedWorkout(new AssignedWorkout(w1.getId(), member.getId(), coachMueller.getId()));

        Workout w2 = saveWorkoutPort.saveWorkout(new Workout("Plan 2", "D", LocalDateTime.now(), coachSchmidt.getId()));
        createAssignedWorkoutPort.createAssignedWorkout(new AssignedWorkout(w2.getId(), member.getId(), coachSchmidt.getId()));


        given()
                .header("Authorization", "Bearer " + token)
                .queryParam("search", "Mueller")
                .when()
                .get("/users/members/" + member.getId() + "/assigned-workouts")
                .then()
                .log().all()
                .statusCode(200)
                .body("data.size()", is(1))
                .body("data[0].coachLink", containsString("users/coaches/" + coachMueller.getId().toString()));
    }

    @Test
    public void shouldReturnEmptyListWhenSearchMatchesNothing() {
        User coach = saveUserPort.save(new Coach("C", "L", "c@ex.com", "pw", Gender.MALE, LocalDate.of(2000, 1, 1)));
        User member = saveUserPort.save(new Member("M", "L", "m@ex.com", "pw", Gender.MALE, LocalDate.of(2000, 1, 1)));
        setupWorkouts(2, coach.getId(), member.getId());
        String token = jwtService.generateToken(member.getId().toString());

        given()
                .header("Authorization", "Bearer " + token)
                .queryParam("search", "NichtExistenterInhalt")
                .when()
                .get("/users/members/" + member.getId() + "/assigned-workouts")
                .then()
                .statusCode(200)
                .body("data.size()", is(0))
                .body("totalCount", is(0));
    }
}

