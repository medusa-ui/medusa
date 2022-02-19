# 🦑 Medusa
[![Documentation Badge](https://img.shields.io/badge/Documentation-medusa--ui.gitbook.io%2Fdocs-informational)](https://medusa-ui.gitbook.io/docs/) [![Codacy Badge](https://app.codacy.com/project/badge/Grade/c59176d4e2a34a50924afa14165071ba)](https://www.codacy.com/gh/medusa-ui/medusa/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=medusa-ui/medusa&amp;utm_campaign=Badge_Grade)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.getmedusa/medusa-ui/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.getmedusa/medusa-ui)

Medusa is a next-generation, easy-to-use, enterprise-ready micro-frontend UI framework in HTML/CSS and Java. Blazingly fast, modern feel and simple to write.

Pages are simply written in HTML and enhanced with Medusa expressions to allow for efficient DOM rewrites. Following the model set forth from Elixir/Phoenix Liveview, all interactivity is handled serverside through WebSockets. The event-driven serverside code is written around non-blocking Spring Reactive. You have to write no Javascript but get full interactivity.

## Usage
```xml
<dependency>
    <groupId>io.getmedusa</groupId>
    <artifactId>medusa-ui</artifactId>
    <version>0.3.5</version>
</dependency>

<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

## Compile
```xml
mvn clean install
```

## Demo

[![Medusa UI Demo](https://yt-embed.herokuapp.com/embed?v=bbZO1FgLSUY)](https://www.youtube.com/watch?v=bbZO1FgLSUY)

Usage documentation: https://medusa-ui.gitbook.io/docs/
