package com.example.symptomchecker.repository;

import com.example.symptomchecker.model.Symptom;
import com.example.symptomchecker.util.LogUtil;
import org.slf4j.Logger;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.List;

@Repository
public class SymptomsRepository {
    private static final Logger log = LogUtil.getLogger(ConditionsRepository.class);

    private final DynamoDbTable<Symptom> symptomTable;
    private final DynamoDbClient dynamoDbClient;

    public SymptomsRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient,
                              DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
        this.symptomTable = dynamoDbEnhancedClient.table("Symptoms", Symptom.getTableSchema());
        ensureTableExists();
    }

    private void ensureTableExists() {
        try {
            dynamoDbClient.describeTable(DescribeTableRequest.builder().tableName("Symptoms").build());
        } catch (ResourceNotFoundException e) {
            log.error("Table Symptoms does not exist. Creating now");

            CreateTableRequest request = CreateTableRequest.builder()
                    .tableName("Symptoms")
                    .keySchema(
                            KeySchemaElement.builder()
                                    .attributeName("symptomName")
                                    .keyType(KeyType.HASH)
                                    .build(),
                            KeySchemaElement.builder()
                                    .attributeName("conditionName")
                                    .keyType(KeyType.RANGE)
                                    .build()
                    )
                    .attributeDefinitions(
                            AttributeDefinition.builder()
                                    .attributeName("symptomName")
                                    .attributeType(ScalarAttributeType.S)
                                    .build(),
                            AttributeDefinition.builder()
                                    .attributeName("conditionName")
                                    .attributeType(ScalarAttributeType.S)
                                    .build()
                    )
                    .provisionedThroughput(ProvisionedThroughput.builder()
                            .readCapacityUnits(5L)
                            .writeCapacityUnits(5L)
                            .build())
                    .build();

            dynamoDbClient.createTable(request);
            waitForTableCreation("Symptoms");
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

    public void saveSymptom(Symptom symptom) {
        symptomTable.putItem(symptom);
    }

    public List<Symptom> getSymptomsByCondition(String conditionId) {
        return symptomTable.scan().items().stream()
                .filter(symptom -> symptom.getConditionName().equals(conditionId))
                .toList();
    }


    public Symptom getSymptomForCondition(String symptomId, String conditionId) {
        return symptomTable.query(r -> r.queryConditional(
                        QueryConditional.keyEqualTo(Key.builder()
                                .partitionValue(symptomId)
                                .sortValue(conditionId)
                                .build())))
                .items()
                .stream()
                .findFirst()
                .orElse(new Symptom(symptomId, conditionId, 0.5));
    }
}

