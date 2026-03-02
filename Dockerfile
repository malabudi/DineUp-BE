# Stage 1: Build  Image
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Stage 2: Setup Runtime Image
FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache bash shadow
RUN useradd -m spring || echo "User already exists"
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
RUN chown -R spring:spring /app
USER spring
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
