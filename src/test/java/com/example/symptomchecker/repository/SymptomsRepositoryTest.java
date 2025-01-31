package com.example.symptomchecker.repository;

import com.example.symptomchecker.model.Symptom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SymptomsRepositoryTest {

    @Mock
    private DynamoDbEnhancedClient dynamoDbEnhancedClient;

    @Mock
    private DynamoDbClient dynamoDbClient;

    @Mock
    private DynamoDbTable<Symptom> symptomTable;

    private SymptomsRepository symptomsRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        doReturn(symptomTable).when(dynamoDbEnhancedClient)
                .table(eq("Symptoms"), any());
        symptomsRepository = new SymptomsRepository(dynamoDbEnhancedClient, dynamoDbClient);
    }

    @Test
    void testSaveSymptom() {
        // Given
        Symptom symptom = new Symptom("Sore Throat", "Common Cold", 0.8);
        doNothing().when(symptomTable).putItem(symptom);

        // When
        symptomsRepository.saveSymptom(symptom);

        // Then
        verify(symptomTable, times(1)).putItem(symptom);
    }

    @Test
    void testGetSymptomsByCondition() {
        // Given
        String conditionId = "Common Cold";
        Symptom symptom1 = new Symptom("Cough", conditionId, 0.7);
        Symptom symptom2 = new Symptom("Runny Nose", conditionId, 0.8);
        List<Symptom> symptomList = List.of(symptom1, symptom2);

        Page<Symptom> mockPage = mock(Page.class);
        when(mockPage.items()).thenReturn(symptomList);

        SdkIterable<Symptom> mockSdkIterable = () -> symptomList.iterator();

        PageIterable<Symptom> mockPageIterable = mock(PageIterable.class);
        when(mockPageIterable.iterator()).thenReturn(List.of(mockPage).iterator());
        when(mockPageIterable.items()).thenReturn(mockSdkIterable);

        when(symptomTable.scan()).thenReturn(mockPageIterable);

        // When
        List<Symptom> result = symptomsRepository.getSymptomsByCondition(conditionId);

        // Then
        assertEquals(2, result.size());
        assertEquals("Cough", result.get(0).getSymptomName());
        assertEquals("Runny Nose", result.get(1).getSymptomName());

        verify(symptomTable, times(1)).scan();
    }

    @Test
    void testGetSymptomsByCondition_Empty() {
        // Given
        List<Symptom> emptyList = List.of();

        Page<Symptom> mockPage = mock(Page.class);
        when(mockPage.items()).thenReturn(emptyList);

        SdkIterable<Symptom> mockSdkIterable = () -> emptyList.iterator();

        PageIterable<Symptom> mockPageIterable = mock(PageIterable.class);
        when(mockPageIterable.iterator()).thenReturn(List.of(mockPage).iterator());
        when(mockPageIterable.items()).thenReturn(mockSdkIterable);

        when(symptomTable.scan()).thenReturn(mockPageIterable);

        // When
        List<Symptom> result = symptomsRepository.getSymptomsByCondition("NonexistentCondition");

        // Then
        assertTrue(result.isEmpty());
        verify(symptomTable, times(1)).scan();
    }
}
