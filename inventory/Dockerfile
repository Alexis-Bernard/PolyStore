#
# Build stage
#
FROM maven:3.8.5-openjdk-17-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -e -f /home/app/pom.xml clean package

#
# Package stage
#
FROM openjdk:17-slim

# Create a non-root user
RUN useradd --create-home rootlessuser
WORKDIR /home/rootlessuser
USER rootlessuser

COPY --from=build /home/app/target/inventory-0.0.1-SNAPSHOT.jar ~/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","~/app.jar"]