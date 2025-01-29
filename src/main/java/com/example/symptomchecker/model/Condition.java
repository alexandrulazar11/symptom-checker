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
public class Condition {
    private String conditionName;
    private double prevalence;

    public static final TableSchema<Condition> TABLE_SCHEMA = StaticTableSchema.builder(Condition.class)
            .newItemSupplier(Condition::new)
            .addAttribute(String.class, a -> a.name("conditionName")
                    .getter(Condition::getConditionName)
                    .setter(Condition::setConditionName)
                    .tags(StaticAttributeTags.primaryPartitionKey()))
            .addAttribute(Double.class, a -> a.name("prevalence")
                    .getter(Condition::getPrevalence)
                    .setter(Condition::setPrevalence))
            .build();
}
