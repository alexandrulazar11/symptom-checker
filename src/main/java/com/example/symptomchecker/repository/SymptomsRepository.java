package com.example.symptomchecker.repository;

import com.example.symptomchecker.model.Symptom;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class SymptomsRepository {

    private final DynamoDbTable<Symptom> symptomTable;

    public SymptomsRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.symptomTable = dynamoDbEnhancedClient.table("Symptoms", Symptom.TABLE_SCHEMA);
    }

    public void saveSymptom(Symptom symptom) {
        symptomTable.putItem(symptom);
    }

    public List<Symptom> getSymptomsByCondition(String conditionId) {
        return symptomTable.scan().items().stream()
                .filter(symptom -> symptom.getConditionName().equals(conditionId))
                .collect(Collectors.toList());
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

