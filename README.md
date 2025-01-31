# Symptom Checker API

## Description
The **Symptom Checker API** is a RESTful service that allows users to register, log in, and perform health assessments by answering symptom-related questions. The API stores data in **AWS DynamoDB** and runs in **Docker containers** for easy deployment.

This should solve the "Works on my machine" problem, creating a robust solution.

## Features
- User authentication (Register/Login)
- Symptom-based assessment process
- Dynamic question flow based on user responses
- Probabilistic condition analysis
- Uses **DynamoDB** for persistence
- **Dockerized** for quick setup
- Importing the Data at start time, from the CSV files

## Getting Started - SetUp
Make sure you have:
- **Docker & Docker Compose** installed
- **Java 21** installed
- **Maven** installed (if running manually)

## Running with Docker

There are 2 containers, one for the DynamoDB and one for the Application. The application one depends and will wait until DynamoDB one is created and also healthy, based on a docker healthcheck.

docker-compose up --build

## Future Work
- Password hashing or encryption
- Making the password transient to not be logged in other scenarios
- Using Keycloak for authentication and authorization
- Caching before the repository layer, since the symptoms and conditions will probably not change too often and to speed up development
- Making the application reactive with Spring WebFlux
- Provide related symptoms to the previous ones
- Scale the list of conditions and symptoms to get rid of the csv files
- Use AI to determine likely conditions
- Use Kubernetes to deploy

