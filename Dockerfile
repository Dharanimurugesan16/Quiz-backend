## Use a JDK image to build the app
#FROM maven:3.9.4-eclipse-temurin-17 AS build
#WORKDIR /app
#COPY src/main/java/com/examly/springapp .
#RUN mvn clean package -DskipTests
#
## Use a lightweight JRE image to run the app
#FROM eclipse-temurin:17-jdk
#WORKDIR /app
#COPY --from=build /app/target/*.jar app.jar
#
## Expose the port (Spring Boot default is 8080)
#EXPOSE 8080
#
#ENTRYPOINT ["java", "-jar", "app.jar"]
FROM maven:3.9.4-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml and download dependencies first (for Docker build caching)
COPY pom.xml .

RUN mvn dependency:go-offline

# Copy the rest of the project (including src/)
COPY . .

# Build the project
RUN mvn clean package -DskipTests

# Use a lightweight JRE for the runtime image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]
