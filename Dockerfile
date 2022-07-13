# Docker multi-stage build

# 1. Building the App with Maven
FROM maven:3-openjdk-18-slim

ADD ./medusa-ui /medusa-ui
WORKDIR /medusa-ui

RUN mvn -B clean install -DskipTests=true

ADD ./medusa-showcase /showcase
WORKDIR /showcase

# Run Maven build
RUN mvn -B clean install -DskipTests=true

# Just using the build artifact and then removing the build-container
FROM openjdk:19-jdk

MAINTAINER Kevin Deyne

VOLUME /tmp

# Add Spring Boot app.jar to Container
COPY --from=0 "/showcase/target/showcase-*-SNAPSHOT.jar" app.jar

# Fire up our Spring Boot app by default
CMD [ "sh", "-c", "java -Dserver.port=$PORT -Xmx300m -Xss512k -XX:CICompilerCount=2 -Dfile.encoding=UTF-8 -XX:+UseContainerSupport -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
