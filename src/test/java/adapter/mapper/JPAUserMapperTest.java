package adapter.mapper;

import adapter.out.Entities.CoachEntity;
import adapter.out.Entities.CoachMemberEntity;
import adapter.out.Entities.MemberEntity;
import adapter.out.Entities.UserEntity;
import domain.model.Coach;
import domain.model.Member;
import domain.valueobject.Gender;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import static adapter.mapper.JPAUserMapper.*;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@Transactional
public class JPAUserMapperTest {
    @Inject
    EntityManager em;

    //helper methods
     void testUserEqualEntities(UserEntity expected, UserEntity actual) {
        if(!expected.getClass().equals(actual.getClass())) throw new RuntimeException("Entities do not match");
        assertEquals(expected.getClass(), actual.getClass());
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getPassword(), actual.getPassword());
        assertEquals(expected.getGender(), actual.getGender());
        assertEquals(expected.getBornOn(), actual.getBornOn());
        assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
        assertEquals(expected.getExercises(), actual.getExercises());
        assertEquals(expected.getWorkouts(), actual.getWorkouts());
    }

    void testCoachEntityEqualEntities(CoachEntity expected, CoachEntity actual) {
         testUserEqualEntities(expected, actual);
         assertEquals(expected.getAssignments().stream().map(e -> e.getMember().getId()).toList(),
                 actual.getAssignments().stream().map(e -> e.getMember().getId()).toList());
    }

    void testMemberEntityEqualEntities(MemberEntity expected, MemberEntity actual) {
        testUserEqualEntities(expected, actual);
        assertEquals(expected.getAssignments().stream().map(e -> e.getCoach().getId()).toList(),
                actual.getAssignments().stream().map(e -> e.getCoach().getId()).toList());
    }

    @Test
    public void MemberToEntityTest(){
        LocalDateTime now = LocalDateTime.now();
        Member user = new Member(null, "Charlie", "Ernst", "charlie101@gmail.com", "Char1234!", Gender.MALE,
                LocalDate.of(1990, 4, 22), now);

        UserEntity expected = new MemberEntity(null, "Charlie", "Ernst", "charlie101@gmail.com", "Char1234!", Gender.MALE,
                LocalDate.of(1990, 4, 22),  now, new ArrayList<>());

        UserEntity actual = toEntity(user);

        testUserEqualEntities(expected, actual);
    }

    @Test
    public void CoachToEntityTest1(){
        LocalDateTime now = LocalDateTime.now();
        Coach user = new Coach(null, "Charlie", "Ernst", "charlie101@gmail.com", "Char1234!", Gender.MALE,
                LocalDate.of(1990, 4, 22), now);

        UserEntity expected = new CoachEntity(null, "Charlie", "Ernst", "charlie101@gmail.com", "Char1234!", Gender.MALE,
                LocalDate.of(1990, 4, 22),  now, new ArrayList<>());

        UserEntity actual = toEntity(user);

        testUserEqualEntities(expected, actual);
    }

    @Test
    public void CoachToEntityTest2(){
        LocalDateTime now = LocalDateTime.now();

        MemberEntity m1 = new MemberEntity(null, "Hello", "world", "helloworld@gmx.de", "1235xyz", Gender.MALE,
                LocalDate.of(2000, 1, 1), now);
        em.persist(m1);

        MemberEntity m2 = new MemberEntity(null,  "Anna", "Krug", "Anna.krug23@outlook.de", "annaK19210", Gender.FEMALE,
                LocalDate.of(1980, 5, 10), now);
        em.persist(m2);

        MemberEntity m3 = new MemberEntity(null, "Ben", "Parker", "benparkerson981@gmail.com", "aUhebn82%!", Gender.MALE,
                LocalDate.of(2005, 11, 11), now);
        em.persist(m3);

        Coach user = new Coach(9L, "Charlie", "Ernst", "charlie101@gmail.com", "Char1234!", Gender.MALE,
                LocalDate.of(1990, 4, 22), now);


        CoachEntity expected = new CoachEntity(9L, "Charlie", "Ernst", "charlie101@gmail.com", "Char1234!", Gender.MALE,
                LocalDate.of(1990, 4, 22),  now);

        CoachMemberEntity cm1 = new CoachMemberEntity(expected, m1);
        CoachMemberEntity cm2 = new CoachMemberEntity(expected, m2);
        CoachMemberEntity cm3 = new CoachMemberEntity(expected, m3);

        user.setClients(Arrays.asList(m1.getId(), m2.getId(), m3.getId()));
        expected.setAssignments(Arrays.asList(cm1, cm2, cm3));

        CoachEntity actual = toEntity(user);

        testUserEqualEntities(expected, actual);
    }

    @Test
    public void CoachEntityMemberEntityObjectTypeMismatchTest(){
        LocalDateTime now = LocalDateTime.now();
        Coach user = new Coach(null, "Charlie", "Ernst", "charlie101@gmail.com", "Char1234!", Gender.MALE,
                LocalDate.of(1990, 4, 22), now);

        UserEntity expected = new MemberEntity(null, "Charlie", "Ernst", "charlie101@gmail.com", "Char1234!", Gender.MALE,
                LocalDate.of(1990, 4, 22),  now, new ArrayList<>());
        UserEntity actual = toEntity(user);
        assertThrows(RuntimeException.class, ()-> testUserEqualEntities(expected, actual));
    }
}

