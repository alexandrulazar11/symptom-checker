package com.example.symptomchecker.repository;

import com.example.symptomchecker.model.Symptom;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

@Repository
public class SymptomsRepository {

    private final DynamoDbTable<Symptom> symptomTable;

    public SymptomsRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.symptomTable = dynamoDbEnhancedClient.table("Symptoms", Symptom.TABLE_SCHEMA);
    }

    public void saveSymptom(Symptom symptom) {
        symptomTable.putItem(symptom);
    }
}

