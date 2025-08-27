FROM openjdk:24-slim-bullseye


COPY target/document-service-0.0.1-SNAPSHOT.jar document-service-0.0.1-SNAPSHOT.jar

RUN apt-get update \
    && apt-get install -y curl \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "document-service-0.0.1-SNAPSHOT.jar"]