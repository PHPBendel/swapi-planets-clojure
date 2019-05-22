FROM openjdk:8-alpine

COPY target/uberjar/sw-planets.jar /sw-planets/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/sw-planets/app.jar"]
