FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

RUN java -version
RUN javac -version
RUN mvn -version

COPY . .
COPY src/main/resources/conditions_data.csv /app/resources/conditions_data.csv
COPY src/main/resources/symptoms_data.csv /app/resources/symptoms_data.csv
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/symptom-checker-0.0.1-SNAPSHOT.jar app.jar
COPY src/main/resources/conditions_data.csv /app/resources/conditions_data.csv
COPY src/main/resources/symptoms_data.csv /app/resources/symptoms_data.csv
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]