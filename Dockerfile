#Multi-stage Docker
#stage 1: build the jar
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

#state 2: run the app
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 9000
ENTRYPOINT ["java", "-jar", "app.jar"]