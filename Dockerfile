# BUILD
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

COPY pom.xml .
RUN mvn dependency:go-offline -q

COPY src ./src
RUN mvn package -DskipTests -q

# RUNTIME
FROM eclipse-temurin:21-jre-alpine AS runner

WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring

RUN mkdir -p /app/data /app/uploads && chown -R spring:spring /app

COPY --from=builder /build/target/*.jar app.jar

USER spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
