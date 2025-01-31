package com.example.symptomchecker.util;

import com.example.symptomchecker.model.Condition;
import com.example.symptomchecker.model.Symptom;
import com.example.symptomchecker.service.DataImportService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

import java.util.List;

@Component
public class DataImportRunner implements CommandLineRunner {

    private static final Logger log = LogUtil.getLogger(DataImportRunner.class);

    private final DataImportService dataImportService;
    private final DynamoDbClient dynamoDbClient;

    @Autowired
    public DataImportRunner(DataImportService dataImportService,
                            DynamoDbClient dynamoDbClient) {
        this.dataImportService = dataImportService;
        this.dynamoDbClient = dynamoDbClient;
    }

    @Override
    public void run(String... args) throws Exception {
        waitForTable("Conditions");
        waitForTable("Symptoms");

        // Import conditions
        ConditionsImporter conditionsImporter = new ConditionsImporter();
        List<Condition> conditions = conditionsImporter.importConditions("conditions_data.csv");
        dataImportService.saveConditions(conditions);

        // Import symptoms
        SymptomsImporter symptomsImporter = new SymptomsImporter();
        List<Symptom> symptoms = symptomsImporter.importSymptoms("symptoms_data.csv");
        dataImportService.saveSymptoms(symptoms);

        log.info("Data imported successfully!");
    }

    private void waitForTable(String tableName) throws InterruptedException {
        while (true) {
            try {
                dynamoDbClient.describeTable(DescribeTableRequest.builder().tableName(tableName).build());
                log.info("Table {} is ready.", tableName);
                break;
            } catch (ResourceNotFoundException e) {
                log.info("Waiting for table {} to be created...", tableName);
                Thread.sleep(2000);
            }
        }
    }
}
