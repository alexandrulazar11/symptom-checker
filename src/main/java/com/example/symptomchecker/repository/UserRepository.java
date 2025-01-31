package com.example.symptomchecker.repository;

import com.example.symptomchecker.model.User;
import com.example.symptomchecker.util.LogUtil;
import org.slf4j.Logger;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

@Repository
public class UserRepository {

    private static final Logger log = LogUtil.getLogger(UserRepository.class);

    private final DynamoDbTable<User> userTable;
    private final DynamoDbClient dynamoDbClient;

    public UserRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient,
                          DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
        this.userTable = dynamoDbEnhancedClient.table("User", User.TABLE_SCHEMA);
        ensureTableExists();
    }

    private void ensureTableExists() {
        try {
            dynamoDbClient.describeTable(DescribeTableRequest.builder().tableName("User").build());
        } catch (ResourceNotFoundException e) {
            log.error("Table User does not exist. Creating now");

            CreateTableRequest request = CreateTableRequest.builder()
                    .tableName("User")
                    .keySchema(
                            KeySchemaElement.builder()
                                    .attributeName("email")
                                    .keyType(KeyType.HASH)
                                    .build()
                    )
                    .attributeDefinitions(
                            AttributeDefinition.builder()
                                    .attributeName("email")
                                    .attributeType(ScalarAttributeType.S)
                                    .build()
                    )
                    .provisionedThroughput(ProvisionedThroughput.builder()
                            .readCapacityUnits(5L)
                            .writeCapacityUnits(5L)
                            .build())
                    .build();

            dynamoDbClient.createTable(request);
            waitForTableCreation("User");
        }
    }

    private void waitForTableCreation(String tableName) {
        while (true) {
            try {
                TableDescription table = dynamoDbClient.describeTable(
                        DescribeTableRequest.builder().tableName(tableName).build()
                ).table();
                if (table.tableStatus().equals(TableStatus.ACTIVE)) {
                    log.info("Table {} is ready. ", tableName);
                    break;
                }
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for table creation", ex);
            }
        }
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
