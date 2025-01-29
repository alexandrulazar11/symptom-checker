package com.example.symptomchecker.controller;

import com.example.symptomchecker.controller.requests.AnswerQuestionRequest;
import com.example.symptomchecker.controller.requests.StartAssessmentRequest;
import com.example.symptomchecker.controller.responses.AssessmentResultResponse;
import com.example.symptomchecker.controller.responses.StartAssessmentResponse;
import com.example.symptomchecker.model.Assessment;
import com.example.symptomchecker.service.AssessmentService;
import com.example.symptomchecker.service.exception.ServiceException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/assessment")
public class AssessmentController {

    private final AssessmentService assessmentService;

    public AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    @PostMapping("/start")
    public ResponseEntity<StartAssessmentResponse> startAssessment(@RequestBody StartAssessmentRequest request) {
        try {
            Assessment assessment = assessmentService.startAssessment(request.userId(), request.initialSymptoms());
            return ResponseEntity.ok(new StartAssessmentResponse(
                    assessment.getAssessmentId(),
                    assessment.getNextQuestionId()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{assessment_id}/answer")
    public ResponseEntity<Map<String, String>> answerQuestion(
            @PathVariable("assessment_id") String assessmentId,
            @RequestBody AnswerQuestionRequest request) {
        try {
            Assessment assessment = assessmentService.answerQuestion(
                    assessmentId,
                    request.questionId(),
                    request.userResponse());

            if (assessment.getNextQuestionId() != null) {
                return ResponseEntity.ok(Map.of("next_question_id", assessment.getNextQuestionId()));
            }

            return ResponseEntity.ok(null);
        } catch (ServiceException e) {
            //TODO: some logging
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{assessment_id}/result")
    public ResponseEntity<AssessmentResultResponse> getAssessmentResult(
            @PathVariable("assessment_id") String assessmentId) {

        try {
            Assessment assessment = assessmentService.getAssessmentResult(assessmentId);

            AssessmentResultResponse response = new AssessmentResultResponse(
                    "mock-condition",
                    Map.of("conditionA", 0.7, "conditionB", 0.2, "conditionC", 0.1)
            );

            return ResponseEntity.ok(response);
        } catch (ServiceException e) {
            //TODO: log
            return ResponseEntity.internalServerError().build();
        }
    }
}
