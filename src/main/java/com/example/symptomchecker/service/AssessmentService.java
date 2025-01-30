package com.example.symptomchecker.service;

import com.example.symptomchecker.model.Assessment;
import com.example.symptomchecker.model.Symptom;
import com.example.symptomchecker.repository.AssessmentRepository;
import com.example.symptomchecker.repository.ConditionsRepository;
import com.example.symptomchecker.repository.SymptomsRepository;
import com.example.symptomchecker.service.exception.ServiceException;
import com.example.symptomchecker.util.LogUtil;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AssessmentService {

    private static final Logger log = LogUtil.getLogger(AssessmentService.class);

    private final AssessmentRepository assessmentRepository;
    private final ConditionsRepository conditionsRepository;
    private final SymptomsRepository symptomsRepository;

    public AssessmentService(AssessmentRepository assessmentRepository,
        ConditionsRepository conditionsRepository,
        SymptomsRepository symptomsRepository) {
        this.assessmentRepository = assessmentRepository;
        this.conditionsRepository = conditionsRepository;
        this.symptomsRepository = symptomsRepository;
    }

    public Assessment startAssessment(String userId, List<String> initialSymptoms) {
        log.info("Starting assessment for user: {}", userId);
        String assessmentId = UUID.randomUUID().toString();

        Assessment assessment = new Assessment(
            assessmentId,
            userId,
            new HashSet<>(initialSymptoms),
            new HashSet<>(),
            new HashMap<>(),
            false);

        conditionsRepository.getAllConditions().forEach(condition ->
            assessment.getConditionProbabilities().put(condition.getConditionName(), condition.getPrevalence()));

        for (String symptom : initialSymptoms) {
            updateProbabilities(assessment, symptom, true);
        }

        log.debug("Initialized probabilities: {}", assessment.getConditionProbabilities());

        assessmentRepository.saveAssessment(assessment);
        return assessment;
    }

    public String answerQuestion(String assessmentId, String symptomId, String response) {
        log.info("Processing answer: Assessment {}, Symptom {}, Response {}",
            assessmentId, symptomId, response);
        Assessment assessment = assessmentRepository.getAssessment(assessmentId);

        if (assessment == null || assessment.isCompleted()) {
            throw new ServiceException("Invalid or completed assessment with ID: " + assessmentId);
        }

        assessment.getAnsweredSymptoms().add(symptomId);

        updateProbabilities(assessment, symptomId, response.equalsIgnoreCase("yes"));

        String nextSymptom = selectNextQuestion(assessment);
        if (assessment.getAnsweredSymptoms().size() >= 3 || nextSymptom == null) {
            log.info("Ending assessment {} - Final probabilities: {}",
                assessmentId, assessment.getConditionProbabilities());
            assessment.setCompleted(true);
        }

        assessmentRepository.saveAssessment(assessment);
        return nextSymptom;
    }

    public Map<String, Double> getAssessmentResult(String assessmentId) {
        log.info("Generating assessment result: {}", assessmentId);
        Assessment assessment = assessmentRepository.getAssessment(assessmentId);

        if (assessment == null) {
            throw new ServiceException("Assessment not found with ID: " + assessmentId);
        }

        if (!assessment.isCompleted()) {
            throw new ServiceException("Assessment is not completed yet.");
        }

        return assessment.getConditionProbabilities();
    }

    private void updateProbabilities(Assessment assessment, String symptomId, boolean answeredYes) {
        Map<String, Double> updatedProbabilities = new HashMap<>();
        double sum = 0.0;

        for (Map.Entry<String, Double> entry : assessment.getConditionProbabilities().entrySet()) {
            String condition = entry.getKey();
            double prior = entry.getValue();

            Symptom symptom = symptomsRepository.getSymptomForCondition(symptomId, condition);
            double likelihood = answeredYes ? symptom.getProbability() : (1 - symptom.getProbability());

            double posterior = likelihood * prior;
            updatedProbabilities.put(condition, posterior);
            sum += posterior;
        }

        for (String condition : updatedProbabilities.keySet()) {
            updatedProbabilities.put(condition, updatedProbabilities.get(condition) / sum);
        }

        assessment.setConditionProbabilities(updatedProbabilities);
    }

    public String selectNextQuestion(Assessment assessment) {
        Set<String> askedSymptoms = assessment.getAnsweredSymptoms();

        String mostLikelyCondition = assessment.getConditionProbabilities().entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);

        if (mostLikelyCondition == null) {
            return null;
        }

        return symptomsRepository.getSymptomsByCondition(mostLikelyCondition).stream()
                .map(Symptom::getSymptomName)
                .filter(symptom -> !askedSymptoms.contains(symptom))
                .findFirst()
                .orElse(null);
    }
}
