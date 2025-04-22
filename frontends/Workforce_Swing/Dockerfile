# Stage 1: Build the project
FROM maven:3.8.6-eclipse-temurin-17 as builder

# Set working directory
WORKDIR /app

# Copy everything
COPY . .

# Optional: give permission to wrapper if used
RUN chmod +x mvnw

# Install all modules
RUN mvn clean install -DskipTests

# Download all dependencies
RUN mvn dependency:go-offline

# Build the project
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jdk
RUN apt-get update && apt-get install -y \
    libx11-6 \
    libxext6 \
    libxrender1 \
    libxtst6 \
    libxi6 \
    && rm -rf /var/lib/apt/lists/*
WORKDIR /app

# Copy the final JAR
COPY --from=builder /app/demo/target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
