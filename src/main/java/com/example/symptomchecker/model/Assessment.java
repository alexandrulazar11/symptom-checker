package com.example.symptomchecker.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Assessment {

    private String assessmentId;
    private String userId;
    private List<String> initialSymptoms;
    private String nextQuestionId;
    private boolean completed;

    // Define the TableSchema for DynamoDB mapping
    public static final TableSchema<Assessment> TABLE_SCHEMA = StaticTableSchema.builder(Assessment.class)
            .newItemSupplier(Assessment::new)
            .addAttribute(String.class, a -> a.name("assessmentId")
                    .getter(Assessment::getAssessmentId)
                    .setter(Assessment::setAssessmentId)
                    .tags(StaticAttributeTags.primaryPartitionKey()))
            .addAttribute(String.class, a -> a.name("userId")
                    .getter(Assessment::getUserId)
                    .setter(Assessment::setUserId))
            .addAttribute(List.class, a -> a.name("initialSymptoms")
                    .getter(Assessment::getInitialSymptoms)
                    .setter(Assessment::setInitialSymptoms))
            .addAttribute(String.class, a -> a.name("nextQuestionId")
                    .getter(Assessment::getNextQuestionId)
                    .setter(Assessment::setNextQuestionId))
            .addAttribute(Boolean.class, a -> a.name("completed")
                    .getter(Assessment::isCompleted)
                    .setter(Assessment::setCompleted))
            .build();
}
