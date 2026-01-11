package application.service;

import application.commands.AssignCoachMemberRelationCommand;
import application.commands.AuthenticatedUser;
import application.port.out.UserPorts.FindCoachMemberRelationPort;
import application.port.out.UserPorts.FindUserByEmailPort;
import application.port.out.UserPorts.SaveCoachMemberRelationPort;
import domain.Results.AssignCoachMemberRelationResult;
import domain.model.Coach;
import domain.model.CoachMember;
import domain.model.Member;
import domain.valueobject.Gender;
import domain.valueobject.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class AssignMemberCoachServiceTest {

    @InjectMocks
    private AssignMemberCoachService service;

    @Mock
    private FindUserByEmailPort findUserByEmailPort;

    @Mock
    private FindCoachMemberRelationPort findCoachMemberRelationPort;

    @Mock
    private SaveCoachMemberRelationPort saveCoachMemberRelationPort;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void assignFailsWhenCoachNotFound() {
        AssignCoachMemberRelationCommand command = new AssignCoachMemberRelationCommand("coach@email.com", "member@email.com",new AuthenticatedUser(1L, UserRole.COACH));

        when(findUserByEmailPort.findByEmail("coach@email.com")).thenReturn(null);

        AssignCoachMemberRelationResult result = service.assign(command);

        assertTrue(result instanceof AssignCoachMemberRelationResult.Failure);
        assertEquals(
                AssignCoachMemberRelationResult.AssignRelationFailureReason.COACH_NOT_FOUND,
                ((AssignCoachMemberRelationResult.Failure) result).reason()
        );
    }

    @Test
    void assignFailsWhenMemberNotFound() {
        AssignCoachMemberRelationCommand command = new AssignCoachMemberRelationCommand("coach@email.com", "member@email.com", new AuthenticatedUser(1L, UserRole.COACH));

        Coach coach = new Coach("name", "lastname", "email", "password", Gender.MALE, LocalDate.now());
        when(findUserByEmailPort.findByEmail("coach@email.com")).thenReturn(coach);
        when(findUserByEmailPort.findByEmail("member@email.com")).thenReturn(null);

        AssignCoachMemberRelationResult result = service.assign(command);

        assertTrue(result instanceof AssignCoachMemberRelationResult.Failure);
        assertEquals(
                AssignCoachMemberRelationResult.AssignRelationFailureReason.MEMBER_NOT_FOUND,
                ((AssignCoachMemberRelationResult.Failure) result).reason()
        );
    }

    @Test
    void assignFailsWhenRelationAlreadyExists() {

        Coach coach = new Coach("name", "lastname", "email", "password", Gender.MALE, LocalDate.now());
        Member member = new Member("name", "lastname", "email2", "password", Gender.MALE, LocalDate.now());
        coach.setId(1L);

        AssignCoachMemberRelationCommand command = new AssignCoachMemberRelationCommand(coach.getEmail(), member.getEmail(),new AuthenticatedUser(1L, UserRole.COACH));

        when(findUserByEmailPort.findByEmail(coach.getEmail())).thenReturn(coach);
        when(findUserByEmailPort.findByEmail(member.getEmail())).thenReturn(member);
        when(findCoachMemberRelationPort.findRelationByCoachAndMember(any(), any()))
                .thenReturn(new CoachMember(1L, 2L));

        AssignCoachMemberRelationResult result = service.assign(command);

        assertTrue(result instanceof AssignCoachMemberRelationResult.Failure);
        assertEquals(
                AssignCoachMemberRelationResult.AssignRelationFailureReason.RELATION_ALREADY_EXISTS,
                ((AssignCoachMemberRelationResult.Failure) result).reason()
        );
    }

    @Test
    void assignSucceedsWhenValid() {

        Coach coach = new Coach("name", "lastname", "email", "password", Gender.MALE, LocalDate.now());
        Member member = new Member("name", "lastname", "email2", "password", Gender.MALE, LocalDate.now());
        coach.setId(1L);
        CoachMember savedRelation = new CoachMember(1L, 2L);
        AssignCoachMemberRelationCommand command = new AssignCoachMemberRelationCommand(coach.getEmail(), member.getEmail(),new AuthenticatedUser(1L, UserRole.COACH));

        when(findUserByEmailPort.findByEmail(coach.getEmail())).thenReturn(coach);
        when(findUserByEmailPort.findByEmail(member.getEmail())).thenReturn(member);
        when(findCoachMemberRelationPort.findRelationByCoachAndMember(any(),any())).thenReturn(null);
        when(saveCoachMemberRelationPort.save(any(CoachMember.class))).thenReturn(savedRelation);

        AssignCoachMemberRelationResult result = service.assign(command);

        assertTrue(result instanceof AssignCoachMemberRelationResult.Success);
        assertEquals(savedRelation, ((AssignCoachMemberRelationResult.Success) result).coachMember());

        verify(saveCoachMemberRelationPort, times(1)).save(any(CoachMember.class));
        verify(findCoachMemberRelationPort, times(1)).findRelationByCoachAndMember(any(), any());
    }
}

