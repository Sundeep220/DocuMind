FROM openjdk:24-slim-bullseye


COPY target/user-service-0.0.1-SNAPSHOT.jar user-service-0.0.1-SNAPSHOT.jar

RUN apt-get update \
    && apt-get install -y curl \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "user-service-0.0.1-SNAPSHOT.jar"]