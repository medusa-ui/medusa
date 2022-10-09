# Docker multi-stage build

# 1. Building the App with Maven
FROM maven:3-amazoncorretto-19 as build

COPY ./medusa-ui /medusa-ui
WORKDIR /medusa-ui

RUN mvn -B clean install -DskipTests=true

RUN yum -y update && yum install -y curl

COPY ./medusa-showcase /showcase
WORKDIR /showcase

# Run Maven build
RUN mvn -B clean install -DskipTests=true

# Just using the build artifact and then removing the build-container
FROM amazoncorretto:19.0.0-alpine

VOLUME /tmp

# Add Spring Boot app.jar to Container
COPY --from=build "/showcase/target/showcase-*-SNAPSHOT.jar" app.jar

# Fire up our Spring Boot app by default
CMD [ "sh", "-c", "java -Dserver.port=$PORT -Dmedusa.hydra.uri=$urlhydra -Dmedusa.hydra.secret.private=$privatekey -Dmedusa.hydra.secret.public=$publickey -Xmx500m -Xss512k -XX:CICompilerCount=2 -Dfile.encoding=UTF-8 -XX:+UseContainerSupport -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]