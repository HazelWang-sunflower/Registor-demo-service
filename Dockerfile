FROM openjdk:17
LABEL maintainer="wyh_sunflower@163.com"

WORKDIR app
COPY target/*jar app.jar

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "app.jar"]