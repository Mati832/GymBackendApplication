package application.service;

import application.commands.member.MemberRegisterCommand;
import application.port.out.UserPorts.FindUserByEmailPort;
import application.port.out.UserPorts.SaveUserPort;
import application.service.member.MemberRegisterService;
import domain.Results.RegisterUserResult;
import domain.model.Member;
import domain.valueobject.Gender;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
public class MemberRegisterServiceTest {
    @Inject
    MemberRegisterService memberRegisterService;
    @InjectMock
    FindUserByEmailPort findUserByEmailPort;
    @InjectMock
    SaveUserPort saveUserPort;

    @Test
    public void testRegisterUser() {
        String email = "email@email";
        MemberRegisterCommand user = new MemberRegisterCommand("firstname", "lastname", email, "password", Gender.MALE, LocalDate.of(2000, 12, 1));
        Member userCopy = new Member("firstname", "lastname", email, "password", Gender.MALE, LocalDate.of(2000, 12, 1));
        when(findUserByEmailPort.findByEmail(email)).thenReturn(null);
        when(saveUserPort.save(any())).thenReturn(userCopy);

        var result = memberRegisterService.registerMember(user);
        assertTrue(result instanceof RegisterUserResult.Success);

        verify(findUserByEmailPort, times(1)).findByEmail(email);
        verify(saveUserPort, times(1)).save(any());
    }

    @Test
    void testRegisterUserEmptyFields() {
        MemberRegisterCommand user = new MemberRegisterCommand("", "", "", "", Gender.MALE, null);

        var registerUserResult = memberRegisterService.registerMember(user);

        assertTrue(registerUserResult instanceof RegisterUserResult.Failure);
        assertEquals(RegisterUserResult.UserRegisterFailureReason.FIELD_EMPTY, ((RegisterUserResult.Failure) registerUserResult).reason());

        verify(saveUserPort, never()).save(any());
    }

    @Test
    void testRegisterUserPasswordTooShort() {
        MemberRegisterCommand user = new MemberRegisterCommand("John", "Doe", "john@example.com", "123", Gender.MALE, LocalDate.of(2000, 1, 1));

        var registerUserResult = memberRegisterService.registerMember(user);
        assertTrue(registerUserResult instanceof RegisterUserResult.Failure);
        assertEquals(RegisterUserResult.UserRegisterFailureReason.PASSWORD_TOO_WEAK, ((RegisterUserResult.Failure) registerUserResult).reason());
        verify(saveUserPort, never()).save(any());
    }

    @Test
    void testRegisterUserBirthDateInFuture() {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        MemberRegisterCommand user = new MemberRegisterCommand("John", "Doe", "john@example.com", "password123", Gender.MALE, futureDate);

        var registerUserResult = memberRegisterService.registerMember(user);
        assertTrue(registerUserResult instanceof RegisterUserResult.Failure);
        assertEquals(RegisterUserResult.UserRegisterFailureReason.INVALID_BIRTHDAY, ((RegisterUserResult.Failure) registerUserResult).reason());

        verify(saveUserPort, never()).save(any());
    }

    @Test
    void testRegisterUserDuplicateEmail() {
        MemberRegisterCommand user = new MemberRegisterCommand("John", "Doe", "john@example.com", "password123", Gender.MALE,
                LocalDate.of(2000, 1, 1));
        Member userCopy = new Member("John", "Doe", "john@example.com", "password123", Gender.MALE,
                LocalDate.of(2000, 1, 1));

        when(findUserByEmailPort.findByEmail(user.email())).thenReturn(userCopy);

        var registerUserResult = memberRegisterService.registerMember(user);
        assertTrue(registerUserResult instanceof RegisterUserResult.Failure);
        assertEquals(RegisterUserResult.UserRegisterFailureReason.USER_ALREADY_EXISTS, ((RegisterUserResult.Failure) registerUserResult).reason());

        verify(findUserByEmailPort, times(1)).findByEmail(user.email());
        verify(saveUserPort, never()).save(any());
    }
}
