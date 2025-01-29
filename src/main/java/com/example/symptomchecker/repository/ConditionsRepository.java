package com.example.symptomchecker.repository;

import com.example.symptomchecker.model.Condition;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

@Repository
public class ConditionsRepository {

    private final DynamoDbTable<Condition> conditionTable;

    public ConditionsRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.conditionTable = dynamoDbEnhancedClient.table("Conditions", Condition.TABLE_SCHEMA);
    }

    public void saveCondition(Condition condition) {
        conditionTable.putItem(condition);
    }
}

