package application.service;

import application.port.out.FindUserByEmailPort;
import application.port.out.SaveUserPort;
import application.service.member.MemberUserService;
import domain.Results.RegisterUserResult;
import domain.model.Member;
import domain.valueobject.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    UserService<Member> userService;
    FindUserByEmailPort findUserByEmailPort;
    SaveUserPort saveUserPort;

    @BeforeEach
    void setup() {
        findUserByEmailPort = mock(FindUserByEmailPort.class);
        saveUserPort = mock(SaveUserPort.class);
        userService = new MemberUserService();
        userService.saveUserPort = saveUserPort;
        userService.findUserByEmailPort = findUserByEmailPort;
    }


    @Test
    public void testRegisterUser() {
        String email = "email@email";
        Member user = new Member("firstname", "lastname", email, "password", Gender.MALE, LocalDateTime.of(2000, 1, 1, 0, 0, 0));
        when(findUserByEmailPort.findByEmail(email)).thenReturn(null);
        when(saveUserPort.save(user)).thenReturn(user);

        var result = userService.registerMember(user);
        assertTrue(result instanceof RegisterUserResult.Success);

        verify(findUserByEmailPort, times(1)).findByEmail(email);
        verify(saveUserPort, times(1)).save(user);
    }

    @Test
    void testRegisterUserEmptyFields() {
        Member user = new Member("", "", "", "", Gender.MALE, null);

        var registerUserResult = userService.registerMember(user);

        assertTrue(registerUserResult instanceof RegisterUserResult.Failure);
        assertEquals(RegisterUserResult.UserFailureReason.FIELD_EMPTY, ((RegisterUserResult.Failure) registerUserResult).reason());

        verify(saveUserPort, never()).save(any());
    }

    @Test
    void testRegisterUserPasswordTooShort() {
        Member user = new Member("John", "Doe", "john@example.com", "123", Gender.MALE, LocalDateTime.of(2000, 1, 1, 0, 0, 0));

        var registerUserResult = userService.registerMember(user);
        assertTrue(registerUserResult instanceof RegisterUserResult.Failure);
        assertEquals(RegisterUserResult.UserFailureReason.PASSWORD_TOO_WEAK, ((RegisterUserResult.Failure) registerUserResult).reason());
        verify(saveUserPort, never()).save(any());
    }

    @Test
    void testRegisterUserBirthDateInFuture() {
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
        Member user = new Member("John", "Doe", "john@example.com", "password123", Gender.MALE, futureDate);

        var registerUserResult = userService.registerMember(user);
        assertTrue(registerUserResult instanceof RegisterUserResult.Failure);
        assertEquals(RegisterUserResult.UserFailureReason.INVALID_BIRTHDAY, ((RegisterUserResult.Failure) registerUserResult).reason());

        verify(saveUserPort, never()).save(any());
    }

    @Test
    void testRegisterUserDuplicateEmail() {
        Member user = new Member("John", "Doe", "john@example.com", "password123", Gender.MALE,
                LocalDateTime.of(2000, 1, 1, 0, 0));

        when(findUserByEmailPort.findByEmail(user.getEmail())).thenReturn(user);

        var registerUserResult = userService.registerMember(user);
        assertTrue(registerUserResult instanceof RegisterUserResult.Failure);
        assertEquals(RegisterUserResult.UserFailureReason.USER_ALREADY_EXISTS, ((RegisterUserResult.Failure) registerUserResult).reason());

        verify(findUserByEmailPort, times(1)).findByEmail(user.getEmail());
        verify(saveUserPort, never()).save(any());
    }
}
