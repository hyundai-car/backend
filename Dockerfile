FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /build

# Build args 정의
ARG DB_URL
ARG DB_USERNAME
ARG DB_PASSWORD
ARG MCFM_KEYCLOAK_SECRET

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN chmod +x gradlew
RUN ./gradlew bootJar

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /build/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]