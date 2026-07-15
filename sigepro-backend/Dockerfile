# Stage 1: Build con Maven oficial (no necesita mvnw)
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime con JRE liviano
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/sigepro-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -Dspring.profiles.active=prod -jar app.jar"]
