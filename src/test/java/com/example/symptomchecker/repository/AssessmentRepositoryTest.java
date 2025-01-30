package com.example.symptomchecker.repository;

import com.example.symptomchecker.model.Assessment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AssessmentRepositoryTest {

    @Mock
    private DynamoDbEnhancedClient dynamoDbEnhancedClient;

    @Mock
    private DynamoDbTable<Assessment> assessmentTable;

    private AssessmentRepository assessmentRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        doReturn(assessmentTable).when(dynamoDbEnhancedClient)
                .table(eq("Assessment"), any());
        assessmentRepository = new AssessmentRepository(dynamoDbEnhancedClient);
    }

    @Test
    void testSaveAssessment_Success() {
        // Given
        Assessment assessment = new Assessment("assessment1", "user", null, null, null, false);
        doNothing().when(assessmentTable).putItem(assessment);

        // When
        assessmentRepository.saveAssessment(assessment);

        // Then
        verify(assessmentTable, times(1)).putItem(assessment);
    }

    @Test
    void testGetAssessment_Success() {
        // Given
        String assessmentId = "assessment1";
        Assessment expectedAssessment = new Assessment(assessmentId, "user", null, null, null, false);
        when(assessmentTable.getItem(any(Key.class))).thenReturn(expectedAssessment);

        // When
        Assessment fetchedAssessment = assessmentRepository.getAssessment(assessmentId);

        // Then
        assertNotNull(fetchedAssessment);
        assertEquals("assessment1", fetchedAssessment.getAssessmentId());
        assertEquals("user", fetchedAssessment.getUserId());
        verify(assessmentTable, times(1)).getItem(any(Key.class));
    }

    @Test
    void testGetAssessment_NotFound() {
        // Given
        String invalidId = "invalid_id";
        when(assessmentTable.getItem(any(Key.class))).thenReturn(null);

        // When
        Assessment fetchedAssessment = assessmentRepository.getAssessment(invalidId);

        // Then
        assertNull(fetchedAssessment);
        verify(assessmentTable, times(1)).getItem(any(Key.class));
    }
}
