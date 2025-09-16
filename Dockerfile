#official openjdk 21 base image
FROM eclipse-temurin:21-jdk-alpine

#working directory
WORKDIR /app

#Copy source and build
COPY target/app.jar app.jar

#expose port
EXPOSE 9000

#Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]