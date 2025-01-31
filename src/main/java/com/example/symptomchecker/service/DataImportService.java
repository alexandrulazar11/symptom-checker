package com.example.symptomchecker.service;

import com.example.symptomchecker.model.Condition;
import com.example.symptomchecker.model.Symptom;
import com.example.symptomchecker.repository.ConditionsRepository;
import com.example.symptomchecker.repository.SymptomsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataImportService {

    private final ConditionsRepository conditionsRepository;
    private final SymptomsRepository symptomsRepository;

    @Autowired
    public DataImportService(ConditionsRepository conditionsRepository,
        SymptomsRepository symptomsRepository) {
        this.conditionsRepository = conditionsRepository;
        this.symptomsRepository = symptomsRepository;
    }

    public void saveConditions(List<Condition> conditions) {
        conditions.forEach(conditionsRepository::saveCondition);
    }

    public void saveSymptoms(List<Symptom> symptoms) {
        symptoms.forEach(symptomsRepository::saveSymptom);
    }
}
