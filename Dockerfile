FROM openjdk:17

WORKDIR app

COPY target/*jar app.jar

ARG DOCKER_CLIENT=docker-17.06.2-ce.tgz

CMD["java", "-jar", "app.jar"]

EXPOSE 8888