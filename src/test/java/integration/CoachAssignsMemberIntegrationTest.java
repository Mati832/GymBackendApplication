package integration;

import adapter.in.services.JwtAdapter;
import application.port.out.UserPorts.FindUserByEmailPort;
import application.port.out.UserPorts.SaveUserPort;
import domain.model.Coach;
import domain.model.Member;
import domain.model.User;
import domain.valueobject.Gender;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class CoachAssignsMemberIntegrationTest {

    @Inject
    EntityManager em;

    @Inject
    SaveUserPort saveUser;
    @Inject
    JwtAdapter jwtService;

    @Inject
    FindUserByEmailPort memberRepository;

    @AfterEach
    @Transactional
    public void tearDown() {
        em.createQuery("delete from CoachMemberEntity ").executeUpdate();
        em.createQuery("delete from MemberEntity").executeUpdate();
        em.createQuery("delete from CoachEntity").executeUpdate();
        em.createQuery("delete from UserEntity").executeUpdate();
    }

    @Test
    public void testAssignCoachSuccess() {
        User coach = saveUser.save(new Coach("firstname", "lastname", "coach@example.com", "password", Gender.MALE, LocalDate.of(2000, 12, 1)));
        User member = saveUser.save(new Member("firstname", "lastname", "member@example.com", "password", Gender.MALE, LocalDate.of(2000, 12, 1)));
        String token = jwtService.generateToken(coach.getId().toString());
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(io.restassured.http.ContentType.JSON)
                .body(
                        """
                                {
                                "coachEmail": "coach@example.com",
                                "memberEmail": "member@example.com"
                                                               }
                                """
                )
                .when()
                .post("/users/coaches/assign")
                .then()
                .statusCode(201);
        User memberFound = memberRepository.findByEmail("member@example.com");
        User coachFound =memberRepository.findByEmail("coach@example.com");
        assertInstanceOf(Member.class, memberFound);
        assertTrue(((Member) memberFound).getCoaches().contains(coachFound.getId()));
    }
    @Test
    public void AssignCoachWithoutAuthentication(){
        User coach = saveUser.save(new Coach("firstname", "lastname", "coach@example.com", "password", Gender.MALE, LocalDate.of(2000, 12, 1)));
        User member = saveUser.save(new Member("firstname", "lastname", "member@example.com", "password", Gender.MALE, LocalDate.of(2000, 12, 1)));
        given()
                .contentType(io.restassured.http.ContentType.JSON)
                .body(
                        """
                                {
                                "coachEmail": "coach@example.com",
                                "memberEmail": "member@example.com"
                                                               }
                                """
                )
                .when()
                .post("/users/coaches/assign")
                .then()
                .statusCode(401);
        User memberFound = memberRepository.findByEmail("member@example.com");
        User coachFound =memberRepository.findByEmail("coach@example.com");
        assertInstanceOf(Member.class, memberFound);
        assertFalse(((Member) memberFound).getCoaches().contains(coachFound.getId()));
    }
    @Test
    public void assignCoachWithoutAuthorization(){
        User memberRequester = saveUser.save(new Member("firstname", "lastname", "requester@example.com", "password", Gender.MALE, LocalDate.of(2000, 12, 1)));
        User coach = saveUser.save(new Coach("firstname", "lastname", "coach@example.com", "password", Gender.MALE, LocalDate.of(2000, 12, 1)));
        User member = saveUser.save(new Member("firstname", "lastname", "member@example.com", "password", Gender.MALE, LocalDate.of(2000, 12, 1)));
        String token = jwtService.generateToken(memberRequester.getId().toString());
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(io.restassured.http.ContentType.JSON)
                .body(
                        """
                                {
                                "coachEmail": "coach@example.com",
                                "memberEmail": "member@example.com"
                                                               }
                                """
                )
                .when()
                .post("/users/coaches/assign")
                .then()
                .statusCode(403);
        User memberFound = memberRepository.findByEmail("member@example.com");
        User coachFound =memberRepository.findByEmail("coach@example.com");
        assertInstanceOf(Member.class, memberFound);
        assertFalse(((Member)memberFound).getCoaches().contains(coachFound.getId()));
    }
}
