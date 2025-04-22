# Sử dụng base image Maven với JDK 8 để build và chạy ứng dụng
FROM maven:3.8.6-jdk-8-slim AS builder

# Thiết lập thư mục làm việc trong container
WORKDIR /app

# Copy các file POM trước để tối ưu cache dependency
COPY pom.xml ./
COPY modal-dialog/pom.xml modal-dialog/
COPY demo/pom.xml demo/

# Tải dependencies để tránh tải lại khi chỉ thay đổi source code
RUN mvn dependency:go-offline -f demo/pom.xml

# Copy toàn bộ source code vào container
COPY modal-dialog/src modal-dialog/src/
COPY demo/src demo/src/

# Build ứng dụng nhưng không đóng gói thành JAR
RUN mvn clean compile -f demo/pom.xml

# Sử dụng cùng base image để chạy ứng dụng bằng Maven
FROM maven:3.8.6-jdk-8-slim

# Thiết lập thư mục làm việc
WORKDIR /app

# Copy toàn bộ source code và build từ stage builder
COPY --from=builder /app /app

# Chạy ứng dụng bằng lệnh exec:java
CMD ["mvn", "exec:java", "-pl", "demo", "-Dexec.mainClass=iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.Demo"]
