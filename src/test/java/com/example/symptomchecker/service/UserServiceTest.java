package com.example.symptomchecker.service;

import com.example.symptomchecker.model.User;
import com.example.symptomchecker.repository.UserRepository;
import com.example.symptomchecker.service.exception.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository);
    }

    @Test
    void testRegisterUser_Success() {
        // Given
        User newUser = new User("test@example.com", "password123", 25, "Male");
        when(userRepository.getUser(newUser.email())).thenReturn(null);
        doNothing().when(userRepository).saveUser(any(User.class));

        // When
        assertDoesNotThrow(() -> userService.registerUser(newUser));

        // Then
        verify(userRepository, times(1)).saveUser(newUser);
    }

    @Test
    void testRegisterUser_UserAlreadyExists() {
        // Given
        User existingUser = new User("test@example.com", "password123", 25, "Male");
        when(userRepository.getUser(existingUser.email())).thenReturn(existingUser);

        // When & Then
        ServiceException exception = assertThrows(ServiceException.class, () -> userService.registerUser(existingUser));
        assertEquals("User already exists with email: test@example.com", exception.getMessage());

        verify(userRepository, never()).saveUser(existingUser);
    }

    @Test
    void testLoginUser_Success() {
        // Given
        String email = "test@example.com";
        String password = "password123";
        User existingUser = new User(email, password, 25, "Male");
        when(userRepository.getUser(email)).thenReturn(existingUser);

        // When
        User loggedInUser = userService.loginUser(email, password);

        // Then
        assertNotNull(loggedInUser);
        assertEquals(email, loggedInUser.email());
        verify(userRepository, times(1)).getUser(email);
    }

    @Test
    void testLoginUser_InvalidPassword() {
        // Given
        String email = "test@example.com";
        String incorrectPassword = "wrongpassword";
        User existingUser = new User(email, "password123", 25, "Male");
        when(userRepository.getUser(email)).thenReturn(existingUser);

        // When & Then
        ServiceException exception = assertThrows(ServiceException.class, () -> userService.loginUser(email, incorrectPassword));
        assertEquals("Invalid email or password.", exception.getMessage());

        verify(userRepository, times(1)).getUser(email);
    }

    @Test
    void testLoginUser_UserNotFound() {
        // Given
        String email = "nonexistent@example.com";
        when(userRepository.getUser(email)).thenReturn(null);

        // When & Then
        ServiceException exception = assertThrows(ServiceException.class, () -> userService.loginUser(email, "password123"));
        assertEquals("Invalid email or password.", exception.getMessage());

        verify(userRepository, times(1)).getUser(email);
    }
}
