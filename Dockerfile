FROM amazoncorretto:17

WORKDIR /app

COPY target/backend-1.jar app.jar

EXPOSE 8081

ENTRYPOINT [ "java", "-jar", "app.jar" ]