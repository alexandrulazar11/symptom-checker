package com.example.symptomchecker.controller;

import com.example.symptomchecker.controller.requests.LoginRequest;
import com.example.symptomchecker.controller.requests.RegisterRequest;
import com.example.symptomchecker.model.User;
import com.example.symptomchecker.service.UserService;
import com.example.symptomchecker.service.exception.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthenticationControllerTest {

    @Mock
    private UserService userService;

    private AuthenticationController authenticationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authenticationController = new AuthenticationController(userService);
    }

    @Test
    void testRegister_Success() {
        // Given
        RegisterRequest request = new RegisterRequest("test@example.com", "password", 25, "Male");
        doNothing().when(userService).registerUser(any(User.class));

        // When
        ResponseEntity<String> response = authenticationController.register(request);

        // Then
        assertEquals(200, response.getStatusCode().value());
        assertEquals("User registered successfully!", response.getBody());
    }

    @Test
    void testRegister_Failure_UserAlreadyExists() {
        // Given
        RegisterRequest request = new RegisterRequest("test@example.com", "password", 30, "Female");

        doThrow(new ServiceException("User already exists")).when(userService).registerUser(any(User.class));

        // When
        ResponseEntity<String> response = authenticationController.register(request);

        // Then
        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void testLogin_Success() {
        // Given
        LoginRequest request = new LoginRequest("test@example.com", "password");
        User user = new User(request.email(), request.password(), 28, "Female");
        when(userService.loginUser(request.email(), request.password())).thenReturn(user);

        // When
        ResponseEntity<String> response = authenticationController.login(request);

        // Then
        assertEquals(200, response.getStatusCode().value());
        assertEquals("test@example.com", response.getBody());
    }

    @Test
    void testLogin_Failure_InvalidCredentials() {
        // Given
        LoginRequest request = new LoginRequest("test@example.com", "wrongPassword");

        doThrow(new ServiceException("Invalid email or password"))
                .when(userService).loginUser(request.email(), request.password());

        // When
        ResponseEntity<String> response = authenticationController.login(request);

        // Then
        assertEquals(500, response.getStatusCode().value());
    }
}
