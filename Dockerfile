FROM openjdk:17
MAINTAINER Adersh
RUN mkdir code
WORKDIR code
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar", "/code/app.jar"]