package integration;

import application.port.out.UserPorts.SaveUserPort;
import domain.model.Member;
import domain.valueobject.Gender;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
public class AuthenticationIntegrationTest {

    @Inject
    EntityManager em;
    @Inject
    SaveUserPort saveUserPort;

    @AfterEach
    @Transactional
    public void tearDown() {
        em.createQuery("delete from CoachMemberEntity ").executeUpdate();
        em.createQuery("delete from MemberEntity").executeUpdate();
        em.createQuery("delete from CoachEntity").executeUpdate();
        em.createQuery("delete from UserEntity").executeUpdate();
    }

    @Test
    void login_should_return_token_when_credentials_are_valid() {
        saveUserPort.save(new Member("Max", "Mustermann", "max.mustermann@gym.de", "sicheresPasswort123", Gender.MALE, LocalDate.of(1995, 5, 15)));
        given()
                .contentType("application/json")
                .body("""
                        {
                          "email": "max.mustermann@gym.de",
                          "password": "sicheresPasswort123"
                        }
                        """)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .header("Authorization", notNullValue());
        //todo dann noch links
    }
}
