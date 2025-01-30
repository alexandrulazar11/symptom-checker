package com.example.symptomchecker.controller;

import com.example.symptomchecker.controller.requests.AnswerQuestionRequest;
import com.example.symptomchecker.controller.requests.StartAssessmentRequest;
import com.example.symptomchecker.controller.responses.AssessmentResultResponse;
import com.example.symptomchecker.controller.responses.StartAssessmentResponse;
import com.example.symptomchecker.service.AssessmentService;
import com.example.symptomchecker.service.exception.ServiceException;
import com.example.symptomchecker.util.LogUtil;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/assessment")
public class AssessmentController {
    private static final Logger log = LogUtil.getLogger(AssessmentController.class);

    private final AssessmentService assessmentService;

    public AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    @PostMapping("/start")
    public ResponseEntity<StartAssessmentResponse> startAssessment(@RequestBody StartAssessmentRequest request) {
        try {
            log.info("Received request to start assessment for user: {}", request.userId());
            var assessment = assessmentService.startAssessment(request.userId(), request.initialSymptoms());
            String nextQuestion = assessmentService.selectNextQuestion(assessment);

            return ResponseEntity.ok(new StartAssessmentResponse(
                    assessment.getAssessmentId(),
                    nextQuestion));
        } catch (Exception e) {
            log.error("Could not start assessment for user: {}", request.userId());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{assessment_id}/answer")
    public ResponseEntity<Map<String, String>> answerQuestion(
        @PathVariable("assessment_id") String assessmentId,
        @RequestBody AnswerQuestionRequest request) {
        log.info("User answered: Assessment {}, Symptom {}, Response {}",
            assessmentId, request.questionId(), request.userResponse());
        try {
            String nextQuestion = assessmentService.answerQuestion(
                assessmentId,
                request.questionId(),
                request.userResponse());

            if (nextQuestion != null) {
                return ResponseEntity.ok(Map.of("next_question_id", nextQuestion));
            }

            return ResponseEntity.ok(null);
        } catch (ServiceException e) {
            log.error("Invalid or completed assessment with ID: {}", assessmentId);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{assessment_id}/result")
    public ResponseEntity<AssessmentResultResponse> getAssessmentResult(
        @PathVariable("assessment_id") String assessmentId) {
        log.info("Fetching final assessment result for: {}", assessmentId);
        try {
            Map<String, Double> conditionProbabilities = assessmentService.getAssessmentResult(assessmentId);

            var likelyCondition = conditionProbabilities.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Unknown");

            AssessmentResultResponse response = new AssessmentResultResponse(
                likelyCondition,
                conditionProbabilities
            );

            return ResponseEntity.ok(response);
        } catch (ServiceException e) {
            log.error("Invalid or not completed assessment with ID: {}", assessmentId);
            return ResponseEntity.internalServerError().build();
        }
    }
}
