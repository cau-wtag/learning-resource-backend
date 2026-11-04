# Stage 1: Build stage with Maven and JDK 17
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /app

# Step 1.1: Copy dependency descriptor and download dependencies offline
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Step 1.2: Copy configuration files and source code
COPY sb-exclude.xml* checkstyle*.xml* ./
COPY src ./src

# Step 1.3: Build the production executable JAR (skip tests and duplicate checks)
RUN mvn clean package -DskipTests -Dspotbugs.skip=true -Dcheckstyle.skip=true

# Stage 2: Lean runtime stage with JRE 17
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the compiled JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose Spring Boot internal port
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]