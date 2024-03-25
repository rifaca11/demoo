package com.cires.technlogies.demo.controllers;
import com.cires.technlogies.demo.controllers.AuthController;
import com.cires.technlogies.demo.dto.AuthRequestDto;
import com.cires.technlogies.demo.dto.JwtResponseDto;
import com.cires.technlogies.demo.services.JwtService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class AuthControllerTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthController authController;

    @Test
    public void testAuthenticateAndGetToken() {
        // Mocking authentication response
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.isAuthenticated()).thenReturn(true);
        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        // Mocking JWT service response
        String accessToken = "mocked-access-token";
        Mockito.when(jwtService.GenerateToken(Mockito.anyString())).thenReturn(accessToken);

        // Creating sample request DTO
        AuthRequestDto requestDto = new AuthRequestDto();
        requestDto.setUsername("testUser");
        requestDto.setPassword("testPassword");

        // Calling the controller method
        JwtResponseDto responseDto = authController.AuthenticateAndGetToken(requestDto);
        // Verifying the response
        assertEquals(accessToken, responseDto.getAccessToken());
    }

    @Test
    public void testAuthenticateAndGetToken_InvalidUser() {
        // Mocking authentication response
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.isAuthenticated()).thenReturn(false);
        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        // Creating sample request DTO
        AuthRequestDto requestDto = new AuthRequestDto();
        requestDto.setUsername("invalidUser");
        requestDto.setPassword("invalidPassword");

        // Verifying that UsernameNotFoundException is thrown
        assertThrows(UsernameNotFoundException.class, () -> authController.AuthenticateAndGetToken(requestDto));
    }
}



