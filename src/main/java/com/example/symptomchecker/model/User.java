package com.example.symptomchecker.model;

import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;

public record User(
        String email,
        String password,
        int age,
        String gender
) {
    public static final TableSchema<User> TABLE_SCHEMA = StaticTableSchema.builder(User.class)
            .newItemSupplier(() -> new User(null, null, 0, null))
            .addAttribute(String.class, a -> a.name("email")
                    .getter(User::email)
                    .setter((u, v) -> new User(v, u.password(), u.age(), u.gender()))
                    .tags(StaticAttributeTags.primaryPartitionKey()))
            .addAttribute(String.class, a -> a.name("password")
                    .getter(User::password)
                    .setter((u, v) -> new User(u.email(), v, u.age(), u.gender())))
            .addAttribute(Integer.class, a -> a.name("age")
                    .getter(User::age)
                    .setter((u, v) -> new User(u.email(), u.password(), v, u.gender())))
            .addAttribute(String.class, a -> a.name("gender")
                    .getter(User::gender)
                    .setter((u, v) -> new User(u.email(), u.password(), u.age(), v)))
            .build();
}
