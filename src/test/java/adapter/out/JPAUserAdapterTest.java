package adapter.out;

import domain.model.Coach;
import domain.model.Member;
import domain.model.User;
import domain.valueobject.Gender;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@QuarkusTest
public class JPAUserAdapterTest {
    @Inject
    EntityManager em;
    @Inject
    JPAUserAdapter JPAUserAdapter;

    @AfterEach @Transactional
    public void tearDown() {
        em.createQuery("delete from CoachMemberEntity ").executeUpdate();
        em.createQuery("delete from MemberEntity").executeUpdate();
        em.createQuery("delete from CoachEntity").executeUpdate();
        em.createQuery("delete from UserEntity").executeUpdate();
    }

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

    @Test
    void createMultipleUser(){
        User user =new Member("name", "lastname", "email", "password", Gender.MALE, LocalDate.now());
        User user2 = new Member("name", "lastname", "email2", "password", Gender.MALE, LocalDate.now());
        User user3 = new Coach("name", "lastname", "email3", "password", Gender.MALE, LocalDate.now());
        User user4 = new Coach("name", "lastname", "email4", "password", Gender.MALE, LocalDate.now());
        JPAUserAdapter.save(user);
        JPAUserAdapter.save(user2);
        JPAUserAdapter.save(user3);
        JPAUserAdapter.save(user4);
        User member = JPAUserAdapter.findByEmail(user.getEmail());
        User member2 = JPAUserAdapter.findByEmail(user2.getEmail());
        User coach = JPAUserAdapter.findByEmail(user3.getEmail());
        User coach2 = JPAUserAdapter.findByEmail(user4.getEmail());
        Assertions.assertNotNull(member);
        Assertions.assertNotNull(member2);
        Assertions.assertNotNull(coach);
        Assertions.assertNotNull(coach2);
    }

    @Test
    void emailAlreadyExistsTest() {
        User user = new Member("name", "lastname", "email", "password", Gender.MALE, LocalDate.now());
        JPAUserAdapter.save(user);
        User user2= new Member("name", "lastname", "email", "password", Gender.MALE, LocalDate.now());
        try {
            JPAUserAdapter.save(user2);
            fail();
        }
        catch (ConstraintViolationException e) {}
    }

    @Test
    void assignedCoachMemberTest(){
        Member member=new Member("name", "lastname", "email", "password", Gender.MALE, LocalDate.now());
        Coach coach =new Coach("name", "lastname", "email2", "password", Gender.MALE, LocalDate.now());
        JPAUserAdapter.save(coach);
        member.getCoaches().add(JPAUserAdapter.findByEmail(coach.getEmail()).getId());
        JPAUserAdapter.save(member);
        Member memberFound = (Member)JPAUserAdapter.findByEmail(member.getEmail());
        Coach coachFound = (Coach) JPAUserAdapter.findByEmail(coach.getEmail());

        Assertions.assertNotNull(memberFound.getCoaches().getFirst());
        Assertions.assertNotNull(coachFound.getClients().getFirst());
        Assertions.assertEquals(coachFound.getId(), memberFound.getCoaches().getFirst());
        Assertions.assertEquals(memberFound.getId(), coachFound.getClients().getFirst());
    }

    @Test @Transactional
    void assignExistingCoachToExistingMemberTest(){
        Member member= new Member("name", "lastname", "email", "password", Gender.MALE, LocalDate.now());
        Coach coach= new Coach("name", "lastname", "email2", "password", Gender.MALE, LocalDate.now());
        JPAUserAdapter.save(member);
        User saved=JPAUserAdapter.save(coach);
        Member memberF = (Member)JPAUserAdapter.findByEmail(member.getEmail());
        memberF.getCoaches().add(saved.getId());
        //JPAUserAdapter.update(memberF);

        Member memberFound = (Member)JPAUserAdapter.findByEmail(member.getEmail());
        Coach coachFound = (Coach) JPAUserAdapter.findByEmail(coach.getEmail());

        Assertions.assertNotNull(memberFound.getCoaches().getFirst());
        Assertions.assertNotNull(coachFound.getClients().getFirst());
        Assertions.assertEquals(coachFound.getId(), memberFound.getCoaches().getFirst());
        Assertions.assertEquals(memberFound.getId(), coachFound.getClients().getFirst());
    }
}
