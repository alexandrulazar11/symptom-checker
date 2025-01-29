package com.example.symptomchecker.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Symptom {
    private String symptomName;
    private String conditionName;
    private double probability;

    public static final TableSchema<Symptom> TABLE_SCHEMA = StaticTableSchema.builder(Symptom.class)
            .newItemSupplier(Symptom::new)
            .addAttribute(String.class, a -> a.name("symptomName")
                    .getter(Symptom::getSymptomName)
                    .setter(Symptom::setSymptomName)
                    .tags(StaticAttributeTags.primaryPartitionKey())) // Partition Key
            .addAttribute(String.class, a -> a.name("conditionName")
                    .getter(Symptom::getConditionName)
                    .setter(Symptom::setConditionName)
                    .tags(StaticAttributeTags.primarySortKey())) // Sort Key
            .addAttribute(Double.class, a -> a.name("probability")
                    .getter(Symptom::getProbability)
                    .setter(Symptom::setProbability))
            .build();
}