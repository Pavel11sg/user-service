FROM eclipse-temurin:21-jdk AS builder
RUN apt-get update && apt-get install -y maven


WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests


FROM eclipse-temurin:21-jre-jammy AS final
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
COPY src/main/resources/db.changelog ./db.changelog
ENTRYPOINT ["java", "-jar", "app.jar"]