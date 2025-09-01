#run.sh
#!/bin/bash
# Load environment variables from .env
export $(grep -v '^#' .env | xargs)

# Run Spring Boot app using Maven
#./mvnw spring-boot:run
./mvnw test

