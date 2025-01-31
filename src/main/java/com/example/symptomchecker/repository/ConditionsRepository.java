package com.example.symptomchecker.repository;

import com.example.symptomchecker.model.Condition;
import com.example.symptomchecker.util.LogUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import java.util.List;

@Repository
public class ConditionsRepository {
    private static final Logger log = LogUtil.getLogger(ConditionsRepository.class);

    private final DynamoDbTable<Condition> conditionTable;
    private final DynamoDbClient dynamoDbClient;

    @Autowired
    public ConditionsRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient,
                                DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
        this.conditionTable = dynamoDbEnhancedClient.table("Conditions", Condition.getTableSchema());
        ensureTableExists();
    }

    private void ensureTableExists() {
        try {
            dynamoDbClient.describeTable(DescribeTableRequest.builder().tableName("Conditions").build());
        } catch (ResourceNotFoundException e) {
            log.error("Table Conditions does not exist. Creating now");

            CreateTableRequest request = CreateTableRequest.builder()
                    .tableName("Conditions")
                    .keySchema(KeySchemaElement.builder()
                            .attributeName("conditionName")
                            .keyType(KeyType.HASH)
                            .build())
                    .attributeDefinitions(
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
            waitForTableCreation("Conditions");
        }
    }

    private void waitForTableCreation(String tableName) {
        while (true) {
            try {
                TableDescription table = dynamoDbClient.describeTable(
                        DescribeTableRequest.builder().tableName(tableName).build()
                ).table();
                if (table.tableStatus().equals(TableStatus.ACTIVE)) {
                    log.info("Table {} is ready. ", tableName);
                    break;
                }
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for table creation", ex);
            }
        }
    }

    public void saveCondition(Condition condition) {
        conditionTable.putItem(condition);
    }

    public List<Condition> getAllConditions() {
        return conditionTable.scan().items().stream().toList();
    }
}

