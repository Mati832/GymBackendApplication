package application.service;

import application.port.out.FindUserByEmailPort;
import application.port.out.SaveUserPort;
import domain.Results.RegisterUserResult;
import domain.model.User;
import domain.valueobject.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class UserServiceTest {

    UserService userService;
    FindUserByEmailPort findUserByEmailPort;
    SaveUserPort saveUserPort;

    @BeforeEach
    void setup() {
        findUserByEmailPort = mock(FindUserByEmailPort.class);
        saveUserPort = mock(SaveUserPort.class);
        userService = new UserService();
        userService.saveUserPort = saveUserPort;  // assign mock manually
        userService.findUserByEmailPort = findUserByEmailPort;
    }


    @Test
    public void testRegisterUser() {
        String email = "email@email";
        User user = new User("firstname", "lastname", email, "password", Gender.MALE, LocalDateTime.of(2000, 1, 1, 0, 0, 0));
        when(findUserByEmailPort.findByEmail(email)).thenReturn(null);
        when(saveUserPort.save(user)).thenReturn(user);

        var result = userService.registerUser(user);
        assertTrue(result instanceof RegisterUserResult.Success);

        verify(findUserByEmailPort, times(1)).findByEmail(email);
        verify(saveUserPort, times(1)).save(user);
    }

    @Test
    void testRegisterUserEmptyFields() {
        User user = new User("", "", "", "", Gender.MALE, null);

        var registerUserResult = userService.registerUser(user);

        assertTrue(registerUserResult instanceof RegisterUserResult.Failure);
        assertEquals(RegisterUserResult.FailureReason.FIELD_EMPTY, ((RegisterUserResult.Failure) registerUserResult).reason());

        verify(saveUserPort, never()).save(any());
    }

    @Test
    void testRegisterUserPasswordTooShort() {
        User user = new User("John", "Doe", "john@example.com", "123", Gender.MALE, LocalDateTime.of(2000, 1, 1, 0, 0, 0));

        var registerUserResult = userService.registerUser(user);
        assertTrue(registerUserResult instanceof RegisterUserResult.Failure);
        assertEquals(RegisterUserResult.FailureReason.PASSWORD_TOO_WEAK, ((RegisterUserResult.Failure) registerUserResult).reason());
        verify(saveUserPort, never()).save(any());
    }

    @Test
    void testRegisterUserBirthDateInFuture() {
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
        User user = new User("John", "Doe", "john@example.com", "password123", Gender.MALE, futureDate);

        var registerUserResult = userService.registerUser(user);
        assertTrue(registerUserResult instanceof RegisterUserResult.Failure);
        assertEquals(RegisterUserResult.FailureReason.INVALID_BIRTHDAY, ((RegisterUserResult.Failure) registerUserResult).reason());

        verify(saveUserPort, never()).save(any());
    }

    @Test
    void testRegisterUserDuplicateEmail() {
        User user = new User("John", "Doe", "john@example.com", "password123", Gender.MALE,
                LocalDateTime.of(2000, 1, 1, 0, 0));

        when(findUserByEmailPort.findByEmail(user.getEmail())).thenReturn(user);

        var registerUserResult = userService.registerUser(user);
        assertTrue(registerUserResult instanceof RegisterUserResult.Failure);
        assertEquals(RegisterUserResult.FailureReason.USER_ALREADY_EXISTS, ((RegisterUserResult.Failure) registerUserResult).reason());

        verify(findUserByEmailPort, times(1)).findByEmail(user.getEmail());
        verify(saveUserPort, never()).save(any());
    }

}
