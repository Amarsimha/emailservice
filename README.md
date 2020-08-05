# EmailService
The application, a gateway, provides an abstraction between two different email service providers - [SendGrid](https://sendgrid.com/) and [Mailgun](https://www.mailgun.com/). If one of the configured services goes down, service quickly fails over to a different provider.


## Requirements

For building and running the application you need:

- [JDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Maven 3](https://maven.apache.org)

## Running the application locally

There are several ways to run a Spring Boot application on your local machine. One way is to execute the `main` method in the `com.siteminder.emailservice.EmailServiceApplication` class from your IDE.

Alternatively you can use the [Spring Boot Maven plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) like so:

```shell
mvn spring-boot:run
```

## Deploy
Use the `Dockerfile` to build a docker image. I followed the instructions on [Amazon](https://aws.amazon.com/getting-started/hands-on/deploy-docker-containers/) to deploy it on an EC2 instance.
