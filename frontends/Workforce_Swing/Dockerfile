# Sử dụng base image Maven với JDK 8 để build dự án
FROM maven:3.8.6-jdk-8-slim AS builder

# Thiết lập thư mục làm việc trong container
WORKDIR /app

# Copy các file POM
COPY pom.xml ./
COPY modal-dialog/pom.xml ./modal-dialog/
COPY demo/pom.xml ./demo/

# Tải các dependencies mà không build ứng dụng
RUN mvn dependency:go-offline -f demo/pom.xml

# Copy mã nguồn sau khi tải dependencies
COPY modal-dialog/src ./modal-dialog/src/
COPY demo/src ./demo/src/

# Build ứng dụng (module demo)
RUN mvn package -f demo/pom.xml

# Sử dụng base image OpenJDK 8 JRE để chạy ứng dụng
FROM openjdk:8-jre-slim

# Thiết lập thư mục làm việc
WORKDIR /app

# Copy JAR đã build từ stage builder
COPY --from=builder /app/demo/target/modal-dialog-demo-*.jar /app/app.jar

# Chạy ứng dụng
CMD ["java", "-jar", "app.jar"]
