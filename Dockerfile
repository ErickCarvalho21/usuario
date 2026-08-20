FROM gradle:8.14-jdk17 AS BUILD
WORKDIR /app
COPY . .
run gradle build --no-daemon

FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY --from=build /app/build/libs/*  /app/usuario.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/usuario.jar"]