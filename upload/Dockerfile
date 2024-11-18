# Sử dụng OpenJDK 17 làm base image
FROM openjdk:17-jdk-slim

# Tạo thư mục để chứa ứng dụng
WORKDIR /app

# Sao chép tệp jar từ máy cục bộ vào container
COPY target/*.jar *.jar
EXPOSE 8081
# Chạy ứng dụng Spring Boot
ENTRYPOINT ["java", "-jar", "*.jar"]
##
#FROM eclipse-temurin:17-jdk-focal as builder
#WORKDIR /app
#COPY . .


#FROM eclipse-temurin:17-jre-focal
#WORKDIR /app
#COPY --from=builder /app/target/*.jar app.jar
#EXPOSE 9000
#ENTRYPOINT ["java", "-jar", "app.jar"]
