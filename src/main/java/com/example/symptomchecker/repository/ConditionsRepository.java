package com.example.symptomchecker.repository;

import com.example.symptomchecker.model.Condition;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ConditionsRepository {

    private final DynamoDbTable<Condition> conditionTable;

    public ConditionsRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.conditionTable = dynamoDbEnhancedClient.table("Conditions", Condition.getTableSchema());
    }

    public void saveCondition(Condition condition) {
        conditionTable.putItem(condition);
    }

    public List<Condition> getAllConditions() {
        return conditionTable.scan().items().stream().toList();
    }
}

