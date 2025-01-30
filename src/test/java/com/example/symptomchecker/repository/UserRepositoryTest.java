package com.example.symptomchecker.repository;

import com.example.symptomchecker.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserRepositoryTest {

    @Mock
    private DynamoDbEnhancedClient dynamoDbEnhancedClient;

    @Mock
    private DynamoDbTable<User> userTable;

    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        doReturn(userTable).when(dynamoDbEnhancedClient)
                .table(eq("User"), any());
        userRepository = new UserRepository(dynamoDbEnhancedClient);
    }

    @Test
    void testSaveUser_Success() {
        // Given
        User user = new User("test@example.com", "password", 25, "Male");
        doNothing().when(userTable).putItem(user);

        // When
        userRepository.saveUser(user);

        // Then
        verify(userTable, times(1)).putItem(user);
    }

    @Test
    void testGetUser_Success() {
        // Given
        String email = "test@example.com";
        User expectedUser = new User(email, "password", 25, "Male");

        Key mockKey = Key.builder().partitionValue(email).build();
        when(userTable.getItem(mockKey)).thenReturn(expectedUser);

        // When
        User retrievedUser = userRepository.getUser(email);

        // Then
        assertNotNull(retrievedUser);
        assertEquals(expectedUser.email(), retrievedUser.email());
        assertEquals(expectedUser.password(), retrievedUser.password());
        assertEquals(expectedUser.age(), retrievedUser.age());
        assertEquals(expectedUser.gender(), retrievedUser.gender());
        verify(userTable, times(1)).getItem(mockKey);
    }

    @Test
    void testGetUser_NotFound() {
        // Given
        String email = "test@example.com";
        Key mockKey = Key.builder().partitionValue(email).build();
        when(userTable.getItem(mockKey)).thenReturn(null);

        // When
        User retrievedUser = userRepository.getUser(email);

        // Then
        assertNull(retrievedUser);
        verify(userTable, times(1)).getItem(mockKey);
    }
}
