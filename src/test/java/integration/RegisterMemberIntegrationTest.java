package integration;

import application.port.out.UserPorts.FindUserByEmailPort;
import domain.model.Member;
import domain.model.User;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class RegisterMemberIntegrationTest {
    @Inject
    EntityManager em;

    @Inject
    FindUserByEmailPort findUserByEmailPort;

    @AfterEach
    @Transactional
    public void tearDown() {
        em.createQuery("delete from CoachMemberEntity ").executeUpdate();
        em.createQuery("delete from MemberEntity").executeUpdate();
        em.createQuery("delete from CoachEntity").executeUpdate();
        em.createQuery("delete from UserEntity").executeUpdate();
    }

    @Test
    void registerMemberSuccessfully() {
        Map<String, Object> registrationData = Map.of(
                "firstName", "Max",
                "lastName", "Mustermann",
                "email", "max.mustermann@gym.de",
                "password", "sicheresPasswort123",
                "gender", "MALE",
                "bornOn", "1995-05-15"
        );
        Response post = given()
                .contentType(ContentType.JSON)
                .body(registrationData)
                .when()
                .post("/users/members/register");

        User byEmail = findUserByEmailPort.findByEmail(registrationData.get("email").toString());
        assertNotNull(byEmail);
        assertEquals(registrationData.get("firstName"), byEmail.getFirstName());
        assertInstanceOf(Member.class, byEmail);
        post
                .then()
                .statusCode(201);
        //.header("Location", containsString("/users/members/"+ byEmail.getId()));
    }
}
