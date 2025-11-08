package application.service;

import application.port.out.UserPort;
import domain.model.User;
import domain.valueobject.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class UserServiceTest {

    UserService userService;
    UserPort userPort;

    @BeforeEach
    void setup() {
        userPort = mock(UserPort.class);
        userService = new UserService();
        userService.userPort = userPort;  // assign mock manually
    }


    @Test
    public void testRegisterUser() {
        String email = "email@email";
        User user = new User("firstname", "lastname", email, "password", Gender.MALE, LocalDateTime.of(2000, 1, 1, 0, 0, 0));
        when(userPort.findByEmail(email)).thenReturn(null);
        when(userPort.save(user)).thenReturn(user);
        try {
            userService.registerUser(user);
        } catch (IllegalArgumentException e) {
            fail(e.getMessage());
        }
        verify(userPort, times(1)).findByEmail(email);
        verify(userPort, times(1)).save(user);
    }

    @Test
    void testRegisterUserEmptyFields() {
        User user = new User("", "", "", "", Gender.MALE, null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(user));

        assertEquals("Fields cannot be empty", ex.getMessage());
        verify(userPort, never()).save(any());
    }

    @Test
    void testRegisterUserPasswordTooShort() {
        User user = new User("John", "Doe", "john@example.com", "123", Gender.MALE, LocalDateTime.of(2000, 1, 1, 0, 0, 0));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(user));

        assertEquals("Password length should be at least 6 characters", ex.getMessage());
        verify(userPort, never()).save(any());
    }

    @Test
    void testRegisterUserBirthDateInFuture() {
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
        User user = new User("John", "Doe", "john@example.com", "password123", Gender.MALE, futureDate);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(user));

        assertEquals("Birth date cannot be in the future", ex.getMessage());
        verify(userPort, never()).save(any());
    }

    @Test
    void testRegisterUserDuplicateEmail() {
        User user = new User("John", "Doe", "john@example.com", "password123", Gender.MALE,
                LocalDateTime.of(2000, 1, 1, 0, 0));

        when(userPort.findByEmail(user.getEmail())).thenReturn(user);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(user));

        assertEquals("Email already exists", ex.getMessage());
        verify(userPort, times(1)).findByEmail(user.getEmail());
        verify(userPort, never()).save(any());
    }

}
