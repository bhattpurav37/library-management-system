# --- build stage ---
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# copy pom first so dependency resolution is cached across builds unless deps change
COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src src
RUN mvn -B clean package -DskipTests

# --- runtime stage ---
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# non-root, no reason to run this as root
RUN addgroup --system library && adduser --system --ingroup library library
USER library

COPY --from=build /workspace/target/*.jar app.jar

EXPOSE 8881
ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
