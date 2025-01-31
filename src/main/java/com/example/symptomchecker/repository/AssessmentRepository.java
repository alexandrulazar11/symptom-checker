package com.example.symptomchecker.repository;

import com.example.symptomchecker.model.Assessment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

@Repository
public class AssessmentRepository {

    private static final Logger log = LoggerFactory.getLogger(AssessmentRepository.class);

    private final DynamoDbTable<Assessment> assessmentTable;
    private final DynamoDbClient dynamoDbClient;

    public AssessmentRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient,
                                DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
        this.assessmentTable = dynamoDbEnhancedClient.table("Assessment", Assessment.getTableSchema());
        ensureTableExists();
    }

    private void ensureTableExists() {
        try {
            dynamoDbClient.describeTable(DescribeTableRequest.builder().tableName("Assessment").build());
        } catch (ResourceNotFoundException e) {
            log.info("Table 'Assessment' does not exist. Creating now...");

            CreateTableRequest request = CreateTableRequest.builder()
                    .tableName("Assessment")
                    .keySchema(
                            KeySchemaElement.builder()
                                    .attributeName("assessmentId")
                                    .keyType(KeyType.HASH)
                                    .build()
                    )
                    .attributeDefinitions(
                            AttributeDefinition.builder()
                                    .attributeName("assessmentId")
                                    .attributeType(ScalarAttributeType.S)
                                    .build()
                    )
                    .provisionedThroughput(ProvisionedThroughput.builder()
                            .readCapacityUnits(5L)
                            .writeCapacityUnits(5L)
                            .build())
                    .build();

            dynamoDbClient.createTable(request);
            waitForTableCreation("Assessment");
        }
    }

    private void waitForTableCreation(String tableName) {
        while (true) {
            try {
                TableDescription table = dynamoDbClient.describeTable(
                        DescribeTableRequest.builder().tableName(tableName).build()
                ).table();
                if (table.tableStatus().equals(TableStatus.ACTIVE)) {
                    log.info("Table {} is ready.", tableName);
                    break;
                }
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for table creation", ex);
            }
        }
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
