package application.service;

import application.port.out.FindUserByEmailPort;
import application.port.out.SaveUserPort;
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
        try {
            userService.registerUser(user);
        } catch (IllegalArgumentException e) {
            fail(e.getMessage());
        }
        verify(findUserByEmailPort, times(1)).findByEmail(email);
        verify(saveUserPort, times(1)).save(user);
    }

    @Test
    void testRegisterUserEmptyFields() {
        User user = new User("", "", "", "", Gender.MALE, null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(user));

        assertEquals("Fields cannot be empty", ex.getMessage());
        verify(saveUserPort, never()).save(any());
    }

    @Test
    void testRegisterUserPasswordTooShort() {
        User user = new User("John", "Doe", "john@example.com", "123", Gender.MALE, LocalDateTime.of(2000, 1, 1, 0, 0, 0));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(user));

        assertEquals("Password length should be at least 6 characters", ex.getMessage());
        verify(saveUserPort, never()).save(any());
    }

    @Test
    void testRegisterUserBirthDateInFuture() {
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
        User user = new User("John", "Doe", "john@example.com", "password123", Gender.MALE, futureDate);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(user));

        assertEquals("Birth date cannot be in the future", ex.getMessage());
        verify(saveUserPort, never()).save(any());
    }

    @Test
    void testRegisterUserDuplicateEmail() {
        User user = new User("John", "Doe", "john@example.com", "password123", Gender.MALE,
                LocalDateTime.of(2000, 1, 1, 0, 0));

        when(findUserByEmailPort.findByEmail(user.getEmail())).thenReturn(user);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(user));

        assertEquals("Email already exists", ex.getMessage());
        verify(findUserByEmailPort, times(1)).findByEmail(user.getEmail());
        verify(saveUserPort, never()).save(any());
    }

}
