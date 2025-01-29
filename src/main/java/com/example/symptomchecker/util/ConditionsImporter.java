package com.example.symptomchecker.util;

import com.example.symptomchecker.model.Condition;
import com.opencsv.CSVReader;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ConditionsImporter {

    public List<Condition> importConditions(String filePath) throws Exception {
        List<Condition> conditions = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] line;
            boolean isHeader = true;

            while ((line = reader.readNext()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                if (!"condition".equalsIgnoreCase(line[0])) continue;

                String conditionName = line[1];
                double prevalence = Double.parseDouble(line[2]);

                conditions.add(new Condition(conditionName, prevalence));
            }
        }

        return conditions;
    }
}
