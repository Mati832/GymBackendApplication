package adapter.out;

import domain.model.Member;
import domain.model.User;
import domain.valueobject.Gender;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@QuarkusTest
public class JPAUserAdapterTest {
    @Inject
    JPAUserAdapter JPAUserAdapter;

    @Test
    void createUserTest() {
        User user = new Member("name", "lastname", "email", "password", Gender.MALE, LocalDate.now());
        User save = JPAUserAdapter.save(user);
        assertEquals(user.getFirstName(), save.getFirstName());
        assertEquals(user.getLastName(), save.getLastName());
        assertEquals(user.getEmail(), save.getEmail());
        assertEquals(user.getPassword(), save.getPassword());
        assertEquals(user.getGender(), save.getGender());
        assertEquals(user.getBornOn(), save.getBornOn());
        assertNotNull(save.getCreatedAt());
        assertNotNull(save.getId());
    }

    @Test
    void findByEmailTest() {
        User user = new Member("name", "lastname", "email", "password", Gender.MALE, LocalDate.now());
        User save = JPAUserAdapter.save(user);
        User byEmail = JPAUserAdapter.findByEmail(user.getEmail());
        assertEquals(user.getFirstName(), byEmail.getFirstName());
        assertEquals(user.getLastName(), byEmail.getLastName());
        assertEquals(user.getEmail(), byEmail.getEmail());
        assertEquals(user.getPassword(), byEmail.getPassword());
        assertEquals(user.getGender(), byEmail.getGender());
        assertEquals(user.getBornOn(), byEmail.getBornOn());
        assertEquals(save.getCreatedAt().truncatedTo(ChronoUnit.MILLIS), byEmail.getCreatedAt().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(save.getId(), byEmail.getId());
    }
}
