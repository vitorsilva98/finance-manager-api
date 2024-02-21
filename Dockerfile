# Build application
FROM maven:3.9.6 AS build
COPY . /app
WORKDIR /app
RUN mvn clean package -DskipTests

# Run application
FROM openjdk:17-alpine
COPY --from=build /app/target/*.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
