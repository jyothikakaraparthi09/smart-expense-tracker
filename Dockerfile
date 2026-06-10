# --- Stage 1: Build the Application ---
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy the pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the application and skip tests for faster deployment
RUN mvn clean package -DskipTests

# --- Stage 2: Run the Application ---
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy only the compiled .jar file from the build stage
COPY --from=build /app/target/*.jar expensetracker.jar

# Expose the internal port (Render maps this automatically)
EXPOSE 8080

# Execute the application
ENTRYPOINT ["java", "-jar", "expensetracker.jar"]