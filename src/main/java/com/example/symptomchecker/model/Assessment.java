package com.example.symptomchecker.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Assessment {

    private String assessmentId;
    private String userId;
    private Set<String> initialSymptoms = new HashSet<>();
    private Set<String> answeredSymptoms = new HashSet<>();
    private Map<String, Double> conditionProbabilities = new HashMap<>();
    private boolean completed = false;

    public static final TableSchema<Assessment> TABLE_SCHEMA = StaticTableSchema.builder(Assessment.class)
            .newItemSupplier(Assessment::new)
            .addAttribute(String.class, a -> a.name("assessmentId")
                    .getter(Assessment::getAssessmentId)
                    .setter(Assessment::setAssessmentId)
                    .tags(StaticAttributeTags.primaryPartitionKey()))
            .addAttribute(String.class, a -> a.name("userId")
                    .getter(Assessment::getUserId)
                    .setter(Assessment::setUserId))
            .addAttribute(Set.class, a -> a.name("initialSymptoms")
                    .getter(Assessment::getInitialSymptoms)
                    .setter(Assessment::setInitialSymptoms))
            .addAttribute(Set.class, a -> a.name("answeredSymptoms")
                    .getter(Assessment::getAnsweredSymptoms)
                    .setter(Assessment::setAnsweredSymptoms))
            .addAttribute(Map.class, a -> a.name("conditionProbabilities")
                    .getter(Assessment::getConditionProbabilities)
                    .setter(Assessment::setConditionProbabilities))
            .addAttribute(Boolean.class, a -> a.name("completed")
                    .getter(Assessment::isCompleted)
                    .setter(Assessment::setCompleted))
            .build();
}
