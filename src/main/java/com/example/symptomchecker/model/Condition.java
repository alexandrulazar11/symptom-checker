package com.example.symptomchecker.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags;


public class Condition {
    private String conditionName;
    private double prevalence;

    public Condition() {

    }

    public Condition(String conditionName, double prevalence) {
        this.conditionName = conditionName;
        this.prevalence = prevalence;
    }

    public String getConditionName() {
        return conditionName;
    }

    public void setConditionName(String conditionName) {
        this.conditionName = conditionName;
    }

    public double getPrevalence() {
        return prevalence;
    }

    public void setPrevalence(double prevalence) {
        this.prevalence = prevalence;
    }

    private static TableSchema<Condition> tableSchema;

    public static TableSchema<Condition> getTableSchema() {
        if (tableSchema == null) {
            tableSchema = StaticTableSchema.builder(Condition.class)
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
        return tableSchema;
    }
}
