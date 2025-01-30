package com.example.symptomchecker.service;

import com.example.symptomchecker.model.Assessment;
import com.example.symptomchecker.model.Condition;
import com.example.symptomchecker.model.Symptom;
import com.example.symptomchecker.repository.AssessmentRepository;
import com.example.symptomchecker.repository.ConditionsRepository;
import com.example.symptomchecker.repository.SymptomsRepository;
import com.example.symptomchecker.service.exception.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AssessmentServiceTest {

    @Mock
    private AssessmentRepository assessmentRepository;

    @Mock
    private ConditionsRepository conditionsRepository;

    @Mock
    private SymptomsRepository symptomsRepository;

    private AssessmentService assessmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        assessmentService = new AssessmentService(assessmentRepository, conditionsRepository, symptomsRepository);
    }

    @Test
    void testStartAssessment_Success() {
        // Given
        String userId = "user123";
        List<String> initialSymptoms = List.of("Fever", "Cough");

        Condition condition1 = new Condition("COVID-19", 0.7);
        Condition condition2 = new Condition("Common Cold", 0.3);

        when(conditionsRepository.getAllConditions()).thenReturn(List.of(condition1, condition2));
        doNothing().when(assessmentRepository).saveAssessment(any(Assessment.class));

        Symptom feverCovid = new Symptom("Fever", "COVID-19", 0.8);
        Symptom feverCold = new Symptom("Fever", "Common Cold", 0.5);
        Symptom coughCovid = new Symptom("Cough", "COVID-19", 0.6);
        Symptom coughCold = new Symptom("Cough", "Common Cold", 0.4);

        when(symptomsRepository.getSymptomForCondition("Fever", "COVID-19")).thenReturn(feverCovid);
        when(symptomsRepository.getSymptomForCondition("Fever", "Common Cold")).thenReturn(feverCold);
        when(symptomsRepository.getSymptomForCondition("Cough", "COVID-19")).thenReturn(coughCovid);
        when(symptomsRepository.getSymptomForCondition("Cough", "Common Cold")).thenReturn(coughCold);

        // When
        Assessment result = assessmentService.startAssessment(userId, initialSymptoms);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertFalse(result.getConditionProbabilities().isEmpty());
        assertEquals(2, result.getConditionProbabilities().size());
        assertTrue(result.getConditionProbabilities().containsKey("COVID-19"));
        assertTrue(result.getConditionProbabilities().containsKey("Common Cold"));
        verify(assessmentRepository, times(1)).saveAssessment(any(Assessment.class));
    }

    @Test
    void testAnswerQuestion_Success() {
        // Given
        String assessmentId = "assessment123";
        String symptomId = "Fever";
        String response = "yes";

        Assessment mockAssessment = new Assessment(
                assessmentId, "user123",
                new HashSet<>(), new HashSet<>(),
                new HashMap<>(Map.of("COVID-19", 0.7, "Common Cold", 0.3)),
                false
        );

        when(assessmentRepository.getAssessment(assessmentId)).thenReturn(mockAssessment);
        doNothing().when(assessmentRepository).saveAssessment(any());

        Symptom feverCovid = new Symptom("Fever", "COVID-19", 0.8);
        Symptom feverCold = new Symptom("Fever", "Common Cold", 0.5);
        when(symptomsRepository.getSymptomForCondition("Fever", "COVID-19")).thenReturn(feverCovid);
        when(symptomsRepository.getSymptomForCondition("Fever", "Common Cold")).thenReturn(feverCold);

        Symptom nextSymptom = new Symptom("Cough", "COVID-19", 0.6);
        when(symptomsRepository.getSymptomsByCondition("COVID-19")).thenReturn(List.of(nextSymptom));

        // When
        String nextQuestion = assessmentService.answerQuestion(assessmentId, symptomId, response);

        // Then
        assertNotNull(nextQuestion, "Expected a next question but got null");
        assertEquals("Cough", nextQuestion, "Next question should be 'Cough'");
        assertTrue(mockAssessment.getAnsweredSymptoms().contains(symptomId), "Answered symptoms should contain 'Fever'");

        verify(assessmentRepository, times(1)).saveAssessment(mockAssessment);
    }

    @Test
    void testAnswerQuestion_InvalidAssessment() {
        // Given
        String assessmentId = "invalid123";
        when(assessmentRepository.getAssessment(assessmentId)).thenReturn(null);

        // When & Then
        assertThrows(ServiceException.class, () -> assessmentService.answerQuestion(assessmentId, "Cough", "yes"));
    }

    @Test
    void testGetAssessmentResult_Success() {
        // Given
        String assessmentId = "assessment123";
        Map<String, Double> probabilities = Map.of("COVID-19", 0.7, "Common Cold", 0.3);
        Assessment mockAssessment = new Assessment(assessmentId, "user123", new HashSet<>(), new HashSet<>(), probabilities, true);

        when(assessmentRepository.getAssessment(assessmentId)).thenReturn(mockAssessment);

        // When
        Map<String, Double> result = assessmentService.getAssessmentResult(assessmentId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(0.7, result.get("COVID-19"));
        assertEquals(0.3, result.get("Common Cold"));
    }

    @Test
    void testGetAssessmentResult_NotCompleted() {
        // Given
        String assessmentId = "unfinishedAssessment";
        Assessment mockAssessment = new Assessment(assessmentId, "user123", new HashSet<>(), new HashSet<>(), new HashMap<>(), false);
        when(assessmentRepository.getAssessment(assessmentId)).thenReturn(mockAssessment);

        // When & Then
        assertThrows(ServiceException.class, () -> assessmentService.getAssessmentResult(assessmentId));
    }


}
