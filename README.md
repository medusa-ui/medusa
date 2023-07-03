# 🦑 Medusa
[![Documentation Badge](https://img.shields.io/badge/Documentation-medusa--ui.gitbook.io%2Fdocs-informational)](https://medusa-ui.gitbook.io/docs/) [![Codacy Badge](https://app.codacy.com/project/badge/Grade/c59176d4e2a34a50924afa14165071ba?branch=rewrite-1.0.0)](https://www.codacy.com/gh/medusa-ui/medusa/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=medusa-ui/medusa&amp;utm_campaign=Badge_Grade)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.getmedusa/medusa-ui/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.getmedusa/medusa-ui) [![MIT License](https://img.shields.io/github/license/medusa-ui/medusa)](https://github.com/medusa-ui/medusa/blob/rewrite-1.0.0/LICENSE)


Medusa is an open-source bidirectional micro-frontend framework built in Java, on top of [Spring Reactive](https://spring.io/reactive), [RSocket](https://rsocket.io/) and [Thymeleaf](https://www.thymeleaf.org/). 

It is designed to help teams build complex user interfaces by breaking them down into smaller, more manageable services, or micro-frontends. Use in combination with [Hydra 🐲](https://github.com/medusa-ui/hydra) for a seamless and resilient micro-frontend experience.

Our major goals are to dramatically close the distance between front-end and back-end, remove the need for Javascript for standard usecases, and use bi-directionality to provide an enhanced development and users experience. 

## Quickstart
To get started with Medusa UI, we recommend checking out our [quickstart template repo](https://github.com/medusa-ui/medusa-template). This template provides a basic Medusa UI app that you can use as a starting point for your own development.

## Component Showcase
If you want to see some of the capabilities that Medusa UI offers out of the box, head over to our [component showcase](https://medusa-showcase.onrender.com/). This showcase provides a live demo of the different UI components that you can use in your own apps.

## Manual installation
Add the maven dependency to an existing Spring Reactive project:
```xml
<dependency>
    <groupId>io.getmedusa</groupId>
    <artifactId>medusa-ui</artifactId>
    <version>0.9.3</version>
</dependency>
```
And you're done. After installing Medusa in your app, you can just write Thymeleaf, extend it with Medusa tags, and you'll see your components interacting directly with the controller. You only write Java and HTML.

Our [component showcase](https://medusa-showcase.onrender.com/) page is available with live samples.

## Compile
This repo contains the framework code. It is build up out of two major components: medusa-showcase and medusa-ui.

Medusa-ui is the actual framework. Medusa-showcase is the component showcase deployed for live samples.

You can build both via a simple maven build:
```xml
mvn clean install
```

## Internal Documentation
For more information about the internal workings of Medusa UI, check out our [documentation](https://medusa-ui.github.io/documentation/docs/category/internals/). This documentation provides detailed information about how Medusa UI is built and how it can be used to build powerful micro-frontends.

