FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -B -Pdocker -DskipTests package

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /workspace/target/prime-0.0.1.war /app/prime.war
RUN mkdir -p /data \
    && chown 10001:0 /data \
    && chmod g=u /data
USER 10001:0
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "/app/prime.war", "--spring.profiles.active=docker"]
