package com.example.symptomchecker.repository;

import com.example.symptomchecker.model.Assessment;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

@Repository
public class AssessmentRepository {

    private final DynamoDbTable<Assessment> assessmentTable;

    public AssessmentRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.assessmentTable = dynamoDbEnhancedClient.table("Assessment", Assessment.TABLE_SCHEMA);
    }

    public void saveAssessment(Assessment assessment) {
        assessmentTable.putItem(assessment);
    }

    public Assessment getAssessment(String assessmentId) {
        return assessmentTable.getItem(Key.builder()
                .partitionValue(assessmentId)
                .build());
    }
}
