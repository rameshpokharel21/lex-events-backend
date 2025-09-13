#run.sh
#!/bin/bash
# Load environment variables from .env
export $(grep -v '^#' .env | xargs)

# Run Spring Boot app using Maven
./mvnw spring-boot:run
#./mvnw test
#./mvnw spring-boot:run \
 # -Dspring-boot.run.fork=false \
  #-Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"

