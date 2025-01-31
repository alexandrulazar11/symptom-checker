package com.example.symptomchecker.model;

import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;

public class User {
    private String email;
    private String password;
    private int age;
    private String gender;

    public User(String email, String password, int age, String gender) {
        this.email = email;
        this.password = password;
        this.age = age;
        this.gender = gender;
    }

    public User() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public static final TableSchema<User> TABLE_SCHEMA = StaticTableSchema.builder(User.class)
        .newItemSupplier(User::new)
        .addAttribute(String.class, a -> a.name("email")
            .getter(User::getEmail)
            .setter(User::setEmail)
            .tags(StaticAttributeTags.primaryPartitionKey()))
        .addAttribute(String.class, a -> a.name("password")
            .getter(User::getPassword)
            .setter(User::setPassword))
        .addAttribute(Integer.class, a -> a.name("age")
            .getter(User::getAge)
            .setter(User::setAge))
        .addAttribute(String.class, a -> a.name("gender")
            .getter(User::getGender)
            .setter(User::setGender))
        .build();
}
