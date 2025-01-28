package com.example.symptomchecker.repository;

import com.example.symptomchecker.model.User;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;

@Repository
public class UserRepository {

    private final DynamoDbTable<User> userTable;

    public UserRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.userTable = dynamoDbEnhancedClient.table("User", User.TABLE_SCHEMA);
    }

    public void saveUser(User user) {
        userTable.putItem(user);
    }

    public User getUser(String email) {
        return userTable.getItem(Key.builder()
                .partitionValue(email)
                .build());
    }
}
