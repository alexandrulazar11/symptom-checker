package com.example.symptomchecker.model;

import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.*;

public class Assessment {

    private String assessmentId;
    private String userId;
    private Set<String> initialSymptoms = new HashSet<>();
    private Set<String> answeredSymptoms = new HashSet<>();
    private Map<String, Double> conditionProbabilities = new HashMap<>();
    private boolean completed = false;

    private static TableSchema<Assessment> tableSchema;
    private static final AttributeConverter<Set<String>> SET_CONVERTER = new SetConverter();
    private static final AttributeConverter<Map<String, Double>> MAP_CONVERTER = new MapConverter();

    public Assessment() {

    }

    public Assessment(String assessmentId,
                      String userId,
                      Set<String> initialSymptoms,
                      Set<String> answeredSymptoms,
                      Map<String, Double> conditionProbabilities,
                      boolean completed) {
        this.assessmentId = assessmentId;
        this.userId = userId;
        this.initialSymptoms = initialSymptoms;
        this.answeredSymptoms = answeredSymptoms;
        this.conditionProbabilities = conditionProbabilities;
        this.completed = completed;
    }

    public String getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(String assessmentId) {
        this.assessmentId = assessmentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Set<String> getInitialSymptoms() {
        return initialSymptoms;
    }

    public void setInitialSymptoms(Set<String> initialSymptoms) {
        this.initialSymptoms = initialSymptoms;
    }

    public Set<String> getAnsweredSymptoms() {
        return answeredSymptoms;
    }

    public void setAnsweredSymptoms(Set<String> answeredSymptoms) {
        this.answeredSymptoms = answeredSymptoms;
    }

    public Map<String, Double> getConditionProbabilities() {
        return conditionProbabilities;
    }

    public void setConditionProbabilities(Map<String, Double> conditionProbabilities) {
        this.conditionProbabilities = conditionProbabilities;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public static TableSchema<Assessment> getTableSchema() {
        if (tableSchema == null) {
            tableSchema = StaticTableSchema.builder(Assessment.class)
                    .newItemSupplier(Assessment::new)
                    .addAttribute(String.class, a -> a.name("assessmentId")
                            .getter(Assessment::getAssessmentId)
                            .setter(Assessment::setAssessmentId)
                            .tags(StaticAttributeTags.primaryPartitionKey()))
                    .addAttribute(String.class, a -> a.name("userId")
                            .getter(Assessment::getUserId)
                            .setter(Assessment::setUserId))
                    .addAttribute(EnhancedType.setOf(String.class), a -> a.name("initialSymptoms")
                            .getter(Assessment::getInitialSymptoms)
                            .setter(Assessment::setInitialSymptoms)
                            .attributeConverter(SET_CONVERTER))
                    .addAttribute(EnhancedType.setOf(String.class), a -> a.name("answeredSymptoms")
                            .getter(Assessment::getAnsweredSymptoms)
                            .setter(Assessment::setAnsweredSymptoms)
                            .attributeConverter(SET_CONVERTER))
                    .addAttribute(EnhancedType.mapOf(String.class, Double.class), a -> a.name("conditionProbabilities")
                            .getter(Assessment::getConditionProbabilities)
                            .setter(Assessment::setConditionProbabilities)
                            .attributeConverter(MAP_CONVERTER))
                    .addAttribute(Boolean.class, a -> a.name("completed")
                            .getter(Assessment::isCompleted)
                            .setter(Assessment::setCompleted))
                    .build();
        }
        return tableSchema;
    }

    public static class SetConverter implements AttributeConverter<Set<String>> {

        @Override
        public AttributeValue transformFrom(Set<String> set) {
            return set == null ? AttributeValue.builder().nul(true).build() :
                    AttributeValue.builder().ss(set).build();
        }

        @Override
        public Set<String> transformTo(AttributeValue attributeValue) {
            return attributeValue.ss() == null ? new HashSet<>() : new HashSet<>(attributeValue.ss());
        }

        @Override
        public EnhancedType<Set<String>> type() {
            return EnhancedType.setOf(EnhancedType.of(String.class));
        }

        @Override
        public AttributeValueType attributeValueType() {
            return AttributeValueType.S;
        }
    }

    public static class MapConverter implements AttributeConverter<Map<String, Double>> {

        @Override
        public AttributeValue transformFrom(Map<String, Double> map) {
            if (map == null) {
                return AttributeValue.builder().nul(true).build();
            }
            Map<String, AttributeValue> attributeMap = new HashMap<>();
            for (Map.Entry<String, Double> entry : map.entrySet()) {
                attributeMap.put(entry.getKey(), AttributeValue.builder().n(entry.getValue().toString()).build());
            }
            return AttributeValue.builder().m(attributeMap).build();
        }

        @Override
        public Map<String, Double> transformTo(AttributeValue attributeValue) {
            if (attributeValue.m() == null) {
                return new HashMap<>();
            }
            Map<String, Double> result = new HashMap<>();
            for (Map.Entry<String, AttributeValue> entry : attributeValue.m().entrySet()) {
                result.put(entry.getKey(), Double.valueOf(entry.getValue().n()));
            }
            return result;
        }

        @Override
        public EnhancedType<Map<String, Double>> type() {
            return EnhancedType.mapOf(EnhancedType.of(String.class), EnhancedType.of(Double.class));
        }

        @Override
        public AttributeValueType attributeValueType() {
            return AttributeValueType.M;
        }
    }
}
