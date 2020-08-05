FROM openjdk:8
EXPOSE 8080
ADD target/email-service-0.1.jar email-service.jar
ENTRYPOINT ["java", "-jar", "email-service.jar"]
