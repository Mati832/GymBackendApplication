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

    @Test
    public void getAssignedWorkoutsSuccessfully() {
        User coach = saveUserPort.save(new Coach("firstname", "lastname", "coach@example.com", "password", Gender.MALE, LocalDate.of(2000, 12, 1)));
        User member = saveUserPort.save(new Member("firstname", "lastname", "member@example.com", "password", Gender.MALE, LocalDate.of(2000, 12, 1)));
        String token = jwtService.generateToken(member.getId().toString());
        Workout workout = saveWorkoutPort.saveWorkout(new Workout("workout", "description", LocalDateTime.now(), coach.getId()));
        Workout workout2 = saveWorkoutPort.saveWorkout(new Workout("workout2", "description2", LocalDateTime.now(), member.getId()));
        AssignedWorkout assignedWorkout = createAssignedWorkoutPort.createAssignedWorkout(new AssignedWorkout(workout.getId(), member.getId(), coach.getId()));
        AssignedWorkout assignedWorkout2 = createAssignedWorkoutPort.createAssignedWorkout(new AssignedWorkout(workout2.getId(), member.getId(), coach.getId()));

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(io.restassured.http.ContentType.JSON)
                .when()
                .get("/users/members/" + member.getId() + "/assigned-workouts")
                .then()
                .log().all()
                .statusCode(200);
    }
}
