FROM openjdk:8-jdk-alpine

RUN mkdir /app
WORKDIR /app

COPY target/mediamanager-1.0-SNAPSHOT.jar mediamanager.jar
EXPOSE 8080

CMD ["java", "-jar", "mediamanager.jar"]
