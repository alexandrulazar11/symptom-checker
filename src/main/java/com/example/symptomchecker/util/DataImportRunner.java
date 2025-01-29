package com.example.symptomchecker.util;

import com.example.symptomchecker.model.Condition;
import com.example.symptomchecker.model.Symptom;
import com.example.symptomchecker.service.DataImportService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataImportRunner implements CommandLineRunner {

    private final DataImportService dataImportService;

    public DataImportRunner(DataImportService dataImportService) {
        this.dataImportService = dataImportService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Import conditions
        ConditionsImporter conditionsImporter = new ConditionsImporter();
        List<Condition> conditions = conditionsImporter.importConditions("conditions.csv");
        dataImportService.saveConditions(conditions);

        // Import symptoms
        SymptomsImporter symptomsImporter = new SymptomsImporter();
        List<Symptom> symptoms = symptomsImporter.importSymptoms("symptoms.csv");
        dataImportService.saveSymptoms(symptoms);

        System.out.println("Data imported successfully!");
    }
}
