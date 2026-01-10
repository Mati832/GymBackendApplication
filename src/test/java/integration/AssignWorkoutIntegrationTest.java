package integration;

import adapter.in.services.JwtAdapter;
import application.port.out.AssignedWorkoutPorts.GetAssignedWorkoutsPort;
import application.port.out.UserPorts.SaveCoachMemberRelationPort;
import application.port.out.UserPorts.SaveUserPort;
import application.port.out.WorkoutPorts.SaveWorkoutPort;
import domain.dbResults.PagedResult;
import domain.model.*;
import domain.valueobject.Gender;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class AssignWorkoutIntegrationTest {

    @Inject
    EntityManager em;
    @Inject
    SaveUserPort saveUserPort;
    @Inject
    SaveWorkoutPort saveWorkoutPort;
    @Inject
    JwtAdapter jwtService;
    @Inject
    SaveCoachMemberRelationPort saveCoachMemberRelationPort;
    @Inject
    GetAssignedWorkoutsPort getAssignedWorkoutsPort;


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
    public void AssignWorkoutSuccessfully() {
        User coach = saveUserPort.save(new Coach("firstname", "lastname", "coach@example.com", "password", Gender.MALE, LocalDate.of(2000, 12, 1)));
        User member = saveUserPort.save(new Member("firstname", "lastname", "member@example.com", "password", Gender.MALE, LocalDate.of(2000, 12, 1)));
        String token = jwtService.generateToken(coach.getId().toString());
        Workout workout = saveWorkoutPort.saveWorkout(new Workout("workout", "description", LocalDateTime.now(), coach.getId()));
        saveCoachMemberRelationPort.save(new CoachMember(coach.getId(), member.getId()));

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(io.restassured.http.ContentType.JSON)
                .when()
                .post("/users/coaches/" + coach.getId() + "/members/" + member.getId() + "/workouts/" + workout.getId())
                .then()
                .log().all()
                .statusCode(201);
        PagedResult<AssignedWorkout> pagedResult = getAssignedWorkoutsPort.getAssignedWorkouts(member.getId(), coach.getId(), "",0, 10);
        List<AssignedWorkout> assignedWorkouts = pagedResult.data();

        Assertions.assertEquals(1, assignedWorkouts.size());
        AssignedWorkout first = assignedWorkouts.getFirst();
        Assertions.assertEquals(coach.getId(), first.getCoachId());
        Assertions.assertEquals(member.getId(), first.getMemberId());
        Assertions.assertNotNull(first.getAssignedAt());
        Assertions.assertEquals(1,pagedResult.totalCount());

    }

}
