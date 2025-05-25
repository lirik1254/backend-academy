FROM eclipse-temurin:23-jdk-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080 9400
ENTRYPOINT ["java", "-jar", "app.jar"]
