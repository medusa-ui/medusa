# Docker multi-stage build

# 1. Building the App with Maven
FROM maven:3-openjdk-18-slim as build

COPY ./medusa-ui /medusa-ui
WORKDIR /medusa-ui

RUN mvn -B clean install -DskipTests=true

COPY ./medusa-showcase /showcase
WORKDIR /showcase

# Run Maven build
RUN mvn -B clean install -DskipTests=true

# Just using the build artifact and then removing the build-container
FROM openjdk:19-jdk

VOLUME /tmp

# Add Spring Boot app.jar to Container
COPY --from=build "/showcase/target/showcase-*-SNAPSHOT.jar" app.jar

curl -LO https://github.com/honeycombio/honeycomb-opentelemetry-java/releases/download/v1.3.0/honeycomb-opentelemetry-javaagent-1.3.0.jar

ENV SERVICE_NAME=medusa-showcase
ENV HONEYCOMB_API_KEY=$honeycomb_api
ENV HONEYCOMB_METRICS_DATASET=my-metrics

# Fire up our Spring Boot app by default
CMD [ "sh", "-c", "java -javaagent:honeycomb-opentelemetry-javaagent-1.3.0.jar -Dserver.port=$PORT -Dmedusa.hydra.uri=$urlhydra -Dmedusa.hydra.secret.private=$privatekey -Dmedusa.hydra.secret.public=$publickey -Xmx500m -Xss512k -XX:CICompilerCount=2 -Dfile.encoding=UTF-8 -XX:+UseContainerSupport -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]