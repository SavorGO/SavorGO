# Sử dụng Maven để build trong container
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy file pom.xml trước để cache dependencies
COPY pom.xml ./
RUN mvn dependency:go-offline

# Copy toàn bộ source code vào
COPY . . 

# Build ứng dụng
RUN mvn clean package -DskipTests

# Copy file .jar vào image cuối cùng
FROM openjdk:21-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar UserManagementService.jar
CMD ["java", "-jar", "UserManagementService.jar"]
