package application.service;

import application.port.in.DTOs.UserLoginRequest;
import application.port.out.FindUserByEmailPort;
import domain.Results.LoginUserResult;
import domain.model.Member;
import domain.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Null;

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
        UserLoginRequest correctRequest = new UserLoginRequest("correctEmail", "correctPassword");
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
        UserLoginRequest wrongPasswordRequest = new UserLoginRequest("correctEmail", "wrongPassword");
        User user= new Member( null,null,wrongPasswordRequest.email(),"correctPassword",null,null );
        when(findUserByEmailPort.findByEmail(wrongPasswordRequest.email())).thenReturn(user);
        LoginUserResult loginUserResult = userLoginService.loginUser(wrongPasswordRequest);

        Assertions.assertTrue(loginUserResult instanceof LoginUserResult.Failure);
        Assertions.assertEquals( LoginUserResult.Failure.UserFailureReason.WRONG_PASSWORD, ((LoginUserResult.Failure) loginUserResult).reason());

        verify(findUserByEmailPort, times(1)).findByEmail(wrongPasswordRequest.email());
    }

    @Test
    void testLoginIncorrectEmail(){
        UserLoginRequest wrongEmailRequest = new UserLoginRequest("wrongEmail", "CorrectPassword");
        when(findUserByEmailPort.findByEmail(wrongEmailRequest.email())).thenReturn(null);
        LoginUserResult loginUserResult = userLoginService.loginUser(wrongEmailRequest);

        Assertions.assertTrue(loginUserResult instanceof LoginUserResult.Failure);
        Assertions.assertEquals(LoginUserResult.Failure.UserFailureReason.USER_NOT_FOUND, ((LoginUserResult.Failure) loginUserResult).reason());

        verify(findUserByEmailPort, times(1)).findByEmail(wrongEmailRequest.email());
    }
}
