package com.example.symptomchecker.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags;

public class Symptom {
    private String symptomName;
    private String conditionName;
    private double probability;

    public Symptom() {

    }

    public Symptom(String symptomName, String conditionName, double probability) {
        this.symptomName = symptomName;
        this.conditionName = conditionName;
        this.probability = probability;
    }

    private static TableSchema<Symptom> tableSchema;

    public String getSymptomName() {
        return symptomName;
    }

    public void setSymptomName(String symptomName) {
        this.symptomName = symptomName;
    }

    public String getConditionName() {
        return conditionName;
    }

    public void setConditionName(String conditionName) {
        this.conditionName = conditionName;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public static TableSchema<Symptom> getTableSchema() {
        if (tableSchema == null) {
            tableSchema = StaticTableSchema.builder(Symptom.class)
                    .newItemSupplier(Symptom::new)
                    .addAttribute(String.class, a -> a.name("symptomName")
                            .getter(Symptom::getSymptomName)
                            .setter(Symptom::setSymptomName)
                            .tags(StaticAttributeTags.primaryPartitionKey()))
                    .addAttribute(String.class, a -> a.name("conditionName")
                            .getter(Symptom::getConditionName)
                            .setter(Symptom::setConditionName)
                            .tags(StaticAttributeTags.primarySortKey()))
                    .addAttribute(Double.class, a -> a.name("probability")
                            .getter(Symptom::getProbability)
                            .setter(Symptom::setProbability))
                    .build();
        }
        return tableSchema;
    }
}