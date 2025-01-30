package com.example.symptomchecker.controller;

import com.example.symptomchecker.controller.requests.AnswerQuestionRequest;
import com.example.symptomchecker.controller.requests.StartAssessmentRequest;
import com.example.symptomchecker.controller.responses.AssessmentResultResponse;
import com.example.symptomchecker.controller.responses.StartAssessmentResponse;
import com.example.symptomchecker.model.Assessment;
import com.example.symptomchecker.repository.AssessmentRepository;
import com.example.symptomchecker.service.AssessmentService;
import com.example.symptomchecker.service.exception.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class AssessmentControllerTest {

    @Mock
    private AssessmentService assessmentService;

    @Mock
    private AssessmentRepository assessmentRepository;

    private AssessmentController assessmentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        assessmentController = new AssessmentController(assessmentService);
    }

    @Test
    void testStartAssessment_Success() {
        // given
        String userId = "user123";
        List<String> initialSymptoms = List.of("Fever", "Cough");
        StartAssessmentRequest request = new StartAssessmentRequest(userId, initialSymptoms);
        Assessment assessment = new Assessment("assessment1", userId, Set.copyOf(initialSymptoms), null, null, false);

        doNothing().when(assessmentRepository).saveAssessment(any());
        when(assessmentRepository.getAssessment(any())).thenReturn(assessment);
        when(assessmentService.startAssessment(userId, initialSymptoms)).thenReturn(assessment);
        when(assessmentService.selectNextQuestion(assessment)).thenReturn("Runny Nose");

        // when
        ResponseEntity<StartAssessmentResponse> responseEntity = assessmentController.startAssessment(request);

        // then
        assertEquals(200, responseEntity.getStatusCode().value());
        assertNotNull(responseEntity.getBody());
        assertEquals("assessment1", responseEntity.getBody().assessmentId());
        assertEquals("Runny Nose", responseEntity.getBody().nextQuestionId());
    }

    @Test
    void testStartAssessment_Failure() {
        // given
        String userId = "user123";
        List<String> initialSymptoms = List.of("Fever", "Cough");
        StartAssessmentRequest request = new StartAssessmentRequest(userId, initialSymptoms);

        doNothing().when(assessmentRepository).saveAssessment(any());
        when(assessmentService.startAssessment(userId, initialSymptoms)).thenThrow(new RuntimeException());

        // when
        ResponseEntity<StartAssessmentResponse> responseEntity = assessmentController.startAssessment(request);

        // then
        assertEquals(500, responseEntity.getStatusCode().value());
    }

    @Test
    void testAnswerQuestion_Success() {
        // given
        String assessmentId = "assessment1";
        String questionId = "Cough";
        String response = "yes";
        AnswerQuestionRequest request = new AnswerQuestionRequest(questionId, response);

        when(assessmentService.answerQuestion(assessmentId, questionId, response)).thenReturn("Fever");

        // when
        ResponseEntity<Map<String, String>> responseEntity = assessmentController.answerQuestion(assessmentId, request);

        // then
        assertEquals(200, responseEntity.getStatusCode().value());
        assertNotNull(responseEntity.getBody());
        assertEquals("Fever", responseEntity.getBody().get("next_question_id"));
    }

    @Test
    void testAnswerQuestion_Failure_InvalidAssessment() {
        // given
        String assessmentId = "invalid123";
        String questionId = "Cough";
        String response = "yes";
        AnswerQuestionRequest request = new AnswerQuestionRequest(questionId, response);

        when(assessmentService.answerQuestion(assessmentId, questionId, response))
                .thenThrow(new ServiceException("Invalid assessment"));

        // when
        ResponseEntity<Map<String, String>> responseEntity = assessmentController.answerQuestion(assessmentId, request);

        // then
        assertEquals(500, responseEntity.getStatusCode().value());
    }

    @Test
    void testGetAssessmentResult_Success() {
        // given
        String assessmentId = "assessment1";
        Map<String, Double> probabilities = Map.of("COVID-19", 0.7, "Common Cold", 0.3);

        when(assessmentService.getAssessmentResult(assessmentId)).thenReturn(probabilities);

        // when
        ResponseEntity<AssessmentResultResponse> responseEntity = assessmentController.getAssessmentResult(assessmentId);

        // then
        assertEquals(200, responseEntity.getStatusCode().value());
        assertNotNull(responseEntity.getBody());
        assertEquals("COVID-19", responseEntity.getBody().condition());
        assertEquals(0.7, responseEntity.getBody().probabilities().get("COVID-19"));
    }

    @Test
    void testGetAssessmentResult_Failure_InvalidAssessment() {
        // given
        String assessmentId = "invalid123";

        when(assessmentService.getAssessmentResult(assessmentId)).thenThrow(new ServiceException("Invalid assessment"));

        // when
        ResponseEntity<AssessmentResultResponse> responseEntity = assessmentController.getAssessmentResult(assessmentId);

        // then
        assertEquals(500, responseEntity.getStatusCode().value());
    }

}
