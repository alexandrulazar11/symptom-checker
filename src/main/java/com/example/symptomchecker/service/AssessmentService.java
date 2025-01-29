package com.example.symptomchecker.service;

import com.example.symptomchecker.model.Assessment;
import com.example.symptomchecker.repository.AssessmentRepository;
import com.example.symptomchecker.service.exception.ServiceException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;

    public AssessmentService(AssessmentRepository assessmentRepository) {
        this.assessmentRepository = assessmentRepository;
    }

    public Assessment startAssessment(String userId, List<String> initialSymptoms) {
        String assessmentId = UUID.randomUUID().toString();

        Assessment assessment = new Assessment(assessmentId, userId, initialSymptoms, "mock-next-question", false);

        assessmentRepository.saveAssessment(assessment);

        return assessment;
    }

    public Assessment answerQuestion(String assessmentId, String questionId, String response) {
        Assessment assessment = assessmentRepository.getAssessment(assessmentId);

        if (assessment == null) {
            throw new ServiceException("Assessment not found with ID: " + assessmentId);
        }

        //TODO: Mock logic for processing the answer and updating the next question
        String nextQuestionId = response.equalsIgnoreCase("yes")
                ? "mock-next-question-id"
                : null;

        assessment.setNextQuestionId(nextQuestionId);
        if (nextQuestionId == null) {
            assessment.setCompleted(true);
        }

        assessmentRepository.saveAssessment(assessment);

        return assessment;
    }

    public Assessment getAssessmentResult(String assessmentId) {
        Assessment assessment = assessmentRepository.getAssessment(assessmentId);

        if (assessment == null) {
            throw new ServiceException("Assessment not found with ID: " + assessmentId);
        }

        // TODO: Return the completed assessment (mock result logic)

        if (!assessment.isCompleted()) {
            throw new ServiceException("Assessment is not completed yet.");
        }

        return assessment;
    }
}
