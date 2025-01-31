package com.example.symptomchecker.util;

import com.example.symptomchecker.model.Symptom;
import com.opencsv.CSVReader;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SymptomsImporter {

    public List<Symptom> importSymptoms(String filePath) throws Exception {
        List<Symptom> symptoms = new ArrayList<>();

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);
        if (inputStream == null) {
            throw new FileNotFoundException("File not found: " + filePath);
        }

        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {
            String[] line;
            boolean isHeader = true;

            while ((line = reader.readNext()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                if (!"symptom".equalsIgnoreCase(line[0])) continue;

                String symptomId = line[1];

                symptoms.add(new Symptom(symptomId, "Hayfever", Double.parseDouble(line[2])));
                symptoms.add(new Symptom(symptomId, "COVID-19", Double.parseDouble(line[3])));
                symptoms.add(new Symptom(symptomId, "Common Cold", Double.parseDouble(line[4])));
            }
        }
        return symptoms;
    }
}

