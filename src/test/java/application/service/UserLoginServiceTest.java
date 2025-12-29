package application.service;

import application.commands.UserLoginCommand;
import application.port.out.UserPorts.FindUserByEmailPort;
import domain.Results.LoginUserResult;
import domain.model.Member;
import domain.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class UserLoginServiceTest {
    UserLoginService userLoginService = new UserLoginService();
    FindUserByEmailPort findUserByEmailPort;

    @BeforeEach
    void setUp(){
        this.findUserByEmailPort=mock(FindUserByEmailPort.class);
        userLoginService.findUserByEmailPort=this.findUserByEmailPort;
    }
    @Test
    void testLoginCorrect(){
        UserLoginCommand correctRequest = new UserLoginCommand("correctEmail", "correctPassword");
        User user= new Member( null,null,correctRequest.email(),correctRequest.password(),null,null );
        when(findUserByEmailPort.findByEmail(correctRequest.email())).thenReturn(user);
        LoginUserResult loginUserResult = userLoginService.loginUser(correctRequest);

        Assertions.assertTrue(loginUserResult instanceof LoginUserResult.Success);
        Assertions.assertEquals(((LoginUserResult.Success) loginUserResult).user().getPassword(),correctRequest.password());
        Assertions.assertEquals(((LoginUserResult.Success) loginUserResult).user().getEmail(),correctRequest.email());
        verify(findUserByEmailPort, times(1)).findByEmail(correctRequest.email());
    }

    @Test
    void testLoginIncorrectPassword(){
        UserLoginCommand wrongPasswordRequest = new UserLoginCommand("correctEmail", "wrongPassword");
        User user= new Member( null,null,wrongPasswordRequest.email(),"correctPassword",null,null );
        when(findUserByEmailPort.findByEmail(wrongPasswordRequest.email())).thenReturn(user);
        LoginUserResult loginUserResult = userLoginService.loginUser(wrongPasswordRequest);

        Assertions.assertTrue(loginUserResult instanceof LoginUserResult.Failure);
        Assertions.assertEquals( LoginUserResult.LoginFailureReason.WRONG_PASSWORD, ((LoginUserResult.Failure) loginUserResult).reason());

        verify(findUserByEmailPort, times(1)).findByEmail(wrongPasswordRequest.email());
    }

    @Test
    void testLoginIncorrectEmail(){
        UserLoginCommand wrongEmailRequest = new UserLoginCommand("wrongEmail", "CorrectPassword");
        when(findUserByEmailPort.findByEmail(wrongEmailRequest.email())).thenReturn(null);
        LoginUserResult loginUserResult = userLoginService.loginUser(wrongEmailRequest);

        Assertions.assertTrue(loginUserResult instanceof LoginUserResult.Failure);
        Assertions.assertEquals(LoginUserResult.LoginFailureReason.USER_NOT_FOUND, ((LoginUserResult.Failure) loginUserResult).reason());

        verify(findUserByEmailPort, times(1)).findByEmail(wrongEmailRequest.email());
    }
}
