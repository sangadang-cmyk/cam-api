FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

COPY ./target/cam-api-0.0.1-SNAPSHOT.jar app.jar

EXPOSE ${PORT:-8080}

CMD ["java", "-jar", "app.jar"]
