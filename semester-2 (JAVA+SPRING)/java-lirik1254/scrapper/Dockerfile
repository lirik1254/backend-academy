FROM eclipse-temurin:23-jdk-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8081 9401
ENTRYPOINT ["java", "-jar", "app.jar"]
