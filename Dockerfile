FROM amazoncorretto:17

WORKDIR /app

COPY target/backend.jar app.jar

EXPOSE 8081

ENTRYPOINT [ "java", "-Dspring.profiles.active=prod" ,"-jar", "app.jar" ]