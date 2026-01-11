package integration.narrow;

import application.commands.AssignCoachMemberRelationCommand;
import application.commands.AuthenticatedUser;
import application.port.in.AssignCoachMemberRelationUseCase;
import application.port.out.UserPorts.FindCoachMemberRelationPort;
import application.port.out.UserPorts.FindUserByEmailPort;
import application.port.out.UserPorts.SaveUserPort;
import domain.Results.AssignCoachMemberRelationResult;
import domain.model.Coach;
import domain.model.CoachMember;
import domain.model.Member;
import domain.model.User;
import domain.valueobject.Gender;
import domain.valueobject.UserRole;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static io.smallrye.common.constraint.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class AssignMemberCoachIntegrationTest {
    @Inject
    SaveUserPort saveUserPort;

    @Inject
    AssignCoachMemberRelationUseCase assignCoachMember;

    @Inject
    EntityManager em;

    @Inject
    FindUserByEmailPort findUserByEmailPort;
    @Inject
    FindCoachMemberRelationPort findCoachMemberPort;

    @AfterEach
    @Transactional
    public void tearDown() {
        em.createQuery("delete from CoachMemberEntity ").executeUpdate();
        em.createQuery("delete from MemberEntity").executeUpdate();
        em.createQuery("delete from CoachEntity").executeUpdate();
        em.createQuery("delete from UserEntity").executeUpdate();
    }

    @Test
    @Transactional
    void assignCreatesRelationInDatabase() {
        Coach coach = new Coach("name", "lastname", "email", "password", Gender.MALE, LocalDate.now());
        User requester = saveUserPort.save(coach);

        Member member = new Member("name", "lastname", "email2", "password", Gender.MALE, LocalDate.now());
        saveUserPort.save(member);

        AssignCoachMemberRelationCommand command =
                new AssignCoachMemberRelationCommand(coach.getEmail(), member.getEmail(), new AuthenticatedUser(requester.getId(), UserRole.COACH));


        AssignCoachMemberRelationResult result = assignCoachMember.assign(command);

        assertTrue(result instanceof AssignCoachMemberRelationResult.Success);

        Coach coachFound = (Coach) findUserByEmailPort.findByEmail(coach.getEmail());
        Member memberFound = (Member) findUserByEmailPort.findByEmail(member.getEmail());
        CoachMember coachMember = findCoachMemberPort.findRelationByCoachAndMember(coachFound.getId(), memberFound.getId());


        assertTrue(coachMember != null);
        assertEquals(coachFound.getId(), coachMember.getCoachId());
        assertEquals(memberFound.getId(), coachMember.getMemberId());
        assertNotNull(coachMember.getAssignedAt());
        assertTrue(memberFound.getCoaches().contains(coachFound.getId()));
        assertTrue(coachFound.getClients().contains(memberFound.getId()));
    }

    @Test
    void coachDoesNotExist() {
        Member member = new Member("name", "lastname", "email", "password", Gender.MALE, LocalDate.now());
        User requester = saveUserPort.save(member);
        AssignCoachMemberRelationCommand cmd =
                new AssignCoachMemberRelationCommand("nonexistent@email.com", member.getEmail(), new AuthenticatedUser(requester.getId(), UserRole.MEMBER));

        AssignCoachMemberRelationResult result = assignCoachMember.assign(cmd);

        assertTrue(result instanceof AssignCoachMemberRelationResult.Failure);
        assertEquals(AssignCoachMemberRelationResult.AssignRelationFailureReason.COACH_NOT_FOUND,
                ((AssignCoachMemberRelationResult.Failure) result).reason());

    }

    @Test
    void relationAlreadyExists() {
        Coach coach = new Coach("name", "lastname", "email", "password", Gender.MALE, LocalDate.now());
        Member member = new Member("name", "lastname", "email2", "password", Gender.MALE, LocalDate.now());
        User requester = saveUserPort.save(coach);
        saveUserPort.save(member);
        assignCoachMember.assign(new AssignCoachMemberRelationCommand(coach.getEmail(), member.getEmail(), new AuthenticatedUser(requester.getId(), UserRole.COACH)));

        AssignCoachMemberRelationResult result = assignCoachMember.assign(
                new AssignCoachMemberRelationCommand(coach.getEmail(), member.getEmail(), new AuthenticatedUser(requester.getId(), UserRole.COACH)));

        assertTrue(result instanceof AssignCoachMemberRelationResult.Failure);
        assertEquals(AssignCoachMemberRelationResult.AssignRelationFailureReason.RELATION_ALREADY_EXISTS,
                ((AssignCoachMemberRelationResult.Failure) result).reason());
    }

    @Test
    @Transactional
    void addingManyRelationsToCoachesAndMembers() {

        Coach coach = new Coach("name", "lastname", "email", "password", Gender.MALE, LocalDate.now());
        Coach coach2 = new Coach("name", "lastname", "email1", "password", Gender.MALE, LocalDate.now());
        Coach coach3 = new Coach("name", "lastname", "email2", "password", Gender.MALE, LocalDate.now());
        Coach coach4 = new Coach("name", "lastname", "email3", "password", Gender.MALE, LocalDate.now());

        Member member = new Member("name", "lastname", "email4", "password", Gender.MALE, LocalDate.now());
        Member member2 = new Member("name", "lastname", "email5", "password", Gender.MALE, LocalDate.now());
        Member member3 = new Member("name", "lastname", "email6", "password", Gender.MALE, LocalDate.now());
        Member member4 = new Member("name", "lastname", "email7", "password", Gender.MALE, LocalDate.now());

        User requester = saveUserPort.save(coach);
        User requester2 = saveUserPort.save(coach2);
        User requester3 = saveUserPort.save(coach3);
        User requester4 = saveUserPort.save(coach4);

        saveUserPort.save(member);
        saveUserPort.save(member2);
        saveUserPort.save(member3);
        saveUserPort.save(member4);


        List<AssignCoachMemberRelationCommand> commands = List.of(
                new AssignCoachMemberRelationCommand(coach.getEmail(), member.getEmail(), new AuthenticatedUser(requester.getId(), UserRole.COACH)),
                new AssignCoachMemberRelationCommand(coach.getEmail(), member2.getEmail(), new AuthenticatedUser(requester.getId(), UserRole.COACH)),
                new AssignCoachMemberRelationCommand(coach.getEmail(), member3.getEmail(), new AuthenticatedUser(requester.getId(), UserRole.COACH)),
                new AssignCoachMemberRelationCommand(coach.getEmail(), member4.getEmail(), new AuthenticatedUser(requester.getId(), UserRole.COACH)),
                new AssignCoachMemberRelationCommand(coach2.getEmail(), member.getEmail(),new AuthenticatedUser(requester2.getId(), UserRole.COACH)),
                new AssignCoachMemberRelationCommand(coach2.getEmail(), member2.getEmail(), new AuthenticatedUser(requester2.getId(), UserRole.COACH)),
                new AssignCoachMemberRelationCommand(coach2.getEmail(), member3.getEmail(), new AuthenticatedUser(requester2.getId(), UserRole.COACH)),
                new AssignCoachMemberRelationCommand(coach3.getEmail(), member.getEmail(), new AuthenticatedUser(requester3.getId(), UserRole.COACH)),
                new AssignCoachMemberRelationCommand(coach3.getEmail(), member2.getEmail(), new AuthenticatedUser(requester3.getId(), UserRole.COACH)),
                new AssignCoachMemberRelationCommand(coach4.getEmail(), member.getEmail(), new AuthenticatedUser(requester4.getId(), UserRole.COACH))
        );


        for (AssignCoachMemberRelationCommand cmd : commands) {
            AssignCoachMemberRelationResult result = assignCoachMember.assign(cmd);
            assertTrue(result instanceof AssignCoachMemberRelationResult.Success);
        }

        Coach coachFound = (Coach) findUserByEmailPort.findByEmail(coach.getEmail());
        Coach coach2Found = (Coach) findUserByEmailPort.findByEmail(coach2.getEmail());
        Coach coach3Found = (Coach) findUserByEmailPort.findByEmail(coach3.getEmail());
        Coach coach4Found = (Coach) findUserByEmailPort.findByEmail(coach4.getEmail());

        Member memberFound = (Member) findUserByEmailPort.findByEmail(member.getEmail());
        Member member2Found = (Member) findUserByEmailPort.findByEmail(member2.getEmail());
        Member member3Found = (Member) findUserByEmailPort.findByEmail(member3.getEmail());
        Member member4Found = (Member) findUserByEmailPort.findByEmail(member4.getEmail());

        assertEquals(4, coachFound.getClients().size());
        assertTrue(coachFound.getClients().containsAll(List.of(memberFound.getId(), member2Found.getId(), member3Found.getId(), member4Found.getId())));

        assertEquals(3, coach2Found.getClients().size());
        assertTrue(coach2Found.getClients().containsAll(List.of(memberFound.getId(), member2Found.getId(), member3Found.getId())));

        assertEquals(2, coach3Found.getClients().size());
        assertTrue(coach3Found.getClients().containsAll(List.of(memberFound.getId(), member2Found.getId())));

        assertEquals(1, coach4Found.getClients().size());
        assertTrue(coach4Found.getClients().contains(memberFound.getId()));

        assertEquals(4, memberFound.getCoaches().size());
        assertTrue(memberFound.getCoaches().containsAll(List.of(coachFound.getId(), coach2Found.getId(), coach3Found.getId(), coach4Found.getId())));

        assertEquals(3, member2Found.getCoaches().size());
        assertTrue(member2Found.getCoaches().containsAll(List.of(coachFound.getId(), coach2Found.getId(), coach3Found.getId())));

        assertEquals(2, member3Found.getCoaches().size());
        assertTrue(member3Found.getCoaches().containsAll(List.of(coachFound.getId(), coach2Found.getId())));

        assertEquals(1, member4Found.getCoaches().size());
        assertTrue(member4Found.getCoaches().contains(coachFound.getId()));

        for (Coach coachEntity : List.of(coachFound, coach2Found, coach3Found, coach4Found)) {
            for (Long memberId : coachEntity.getClients()) {
                CoachMember relation = findCoachMemberPort.findRelationByCoachAndMember(coachEntity.getId(), memberId);
                assertNotNull(relation.getAssignedAt());
            }
        }
    }

}
