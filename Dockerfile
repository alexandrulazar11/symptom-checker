FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
RUN apk add --no-cache maven
COPY . .
RUN ./mvnw clean package -DskipTests
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/symptom-checker-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
