package com.example.symptomchecker.service;

import com.example.symptomchecker.model.Condition;
import com.example.symptomchecker.model.Symptom;
import com.example.symptomchecker.repository.ConditionsRepository;
import com.example.symptomchecker.repository.SymptomsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.*;

public class DataImportServiceTest {

    @Mock
    private ConditionsRepository conditionsRepository;

    @Mock
    private SymptomsRepository symptomsRepository;

    private DataImportService dataImportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        dataImportService = new DataImportService(conditionsRepository, symptomsRepository);
    }

    @Test
    void testSaveConditions_Success() {
        // Given
        Condition condition1 = new Condition("COVID-19", 0.85);
        Condition condition2 = new Condition("Common Cold", 0.50);
        List<Condition> conditions = List.of(condition1, condition2);

        doNothing().when(conditionsRepository).saveCondition(any(Condition.class));

        // When
        dataImportService.saveConditions(conditions);

        // Then
        verify(conditionsRepository, times(1)).saveCondition(condition1);
        verify(conditionsRepository, times(1)).saveCondition(condition2);
    }

    @Test
    void testSaveSymptoms_Success() {
        // Given
        Symptom symptom1 = new Symptom("Cough", "COVID-19", 0.7);
        Symptom symptom2 = new Symptom("Fever", "COVID-19", 0.9);
        List<Symptom> symptoms = List.of(symptom1, symptom2);

        doNothing().when(symptomsRepository).saveSymptom(any(Symptom.class));

        // When
        dataImportService.saveSymptoms(symptoms);

        // Then
        verify(symptomsRepository, times(1)).saveSymptom(symptom1);
        verify(symptomsRepository, times(1)).saveSymptom(symptom2);
    }
}
