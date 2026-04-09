FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -Dmaven.compiler.release=17

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/reservas-ms-catalog-service.jar reservas-ms-catalog-service.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "reservas-ms-catalog-service.jar"]