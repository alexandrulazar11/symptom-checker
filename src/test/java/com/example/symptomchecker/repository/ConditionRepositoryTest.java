package com.example.symptomchecker.repository;

import com.example.symptomchecker.model.Condition;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ConditionRepositoryTest {

    @Mock
    private DynamoDbEnhancedClient dynamoDbEnhancedClient;

    @Mock
    private DynamoDbClient dynamoDbClient;

    @Mock
    private DynamoDbTable<Condition> conditionTable;

    private ConditionsRepository conditionsRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        doReturn(conditionTable).when(dynamoDbEnhancedClient)
                .table(eq("Conditions"), any());
        conditionsRepository = new ConditionsRepository(dynamoDbEnhancedClient, dynamoDbClient);
    }

    @Test
    void testSaveCondition_Success() {
        // Given
        Condition condition = new Condition("COVID-19", 0.85);
        doNothing().when(conditionTable).putItem(condition);

        // When
        conditionsRepository.saveCondition(condition);

        // Then
        verify(conditionTable, times(1)).putItem(condition);
    }

    @Test
    void testGetAllConditions_Success() {
        // Given
        Condition condition1 = new Condition("COVID-19", 0.85);
        Condition condition2 = new Condition("Common Cold", 0.50);

        List<Condition> conditionList = List.of(condition1, condition2);

        Page<Condition> mockPage = mock(Page.class);
        when(mockPage.items()).thenReturn(conditionList);

        SdkIterable<Condition> mockSdkIterable = () -> conditionList.iterator();

        PageIterable<Condition> mockPageIterable = mock(PageIterable.class);
        when(mockPageIterable.iterator()).thenReturn(List.of(mockPage).iterator());
        when(mockPageIterable.items()).thenReturn(mockSdkIterable);

        when(conditionTable.scan()).thenReturn(mockPageIterable);

        // When
        List<Condition> retrievedConditions = conditionsRepository.getAllConditions();

        // Then
        assertNotNull(retrievedConditions);
        assertEquals(2, retrievedConditions.size());
        assertEquals("COVID-19", retrievedConditions.get(0).getConditionName());
        assertEquals(0.85, retrievedConditions.get(0).getPrevalence());
        assertEquals("Common Cold", retrievedConditions.get(1).getConditionName());
        assertEquals(0.50, retrievedConditions.get(1).getPrevalence());

        verify(conditionTable, times(1)).scan();
    }

    @Test
    void testGetAllConditions_Empty() {
        // Given
        List<Condition> emptyConditionList = List.of();

        Page<Condition> mockPage = mock(Page.class);
        when(mockPage.items()).thenReturn(emptyConditionList);

        SdkIterable<Condition> mockSdkIterable = () -> emptyConditionList.iterator();

        PageIterable<Condition> mockPageIterable = mock(PageIterable.class);
        when(mockPageIterable.iterator()).thenReturn(List.of(mockPage).iterator());
        when(mockPageIterable.items()).thenReturn(mockSdkIterable);

        when(conditionTable.scan()).thenReturn(mockPageIterable);

        // When
        List<Condition> result = conditionsRepository.getAllConditions();

        // Then
        assertEquals(0, result.size());
        verify(conditionTable, times(1)).scan();
    }
}
