# Stage 1: Build stage with Maven and JDK 17
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /app

# Step 1.1: Copy dependency descriptor and download dependencies offline
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Step 1.2: Copy source code and build the production executable JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Lean runtime stage with JRE 17
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the compiled JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose Spring Boot's internal port
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]