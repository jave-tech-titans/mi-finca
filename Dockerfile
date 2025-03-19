FROM openjdk:17-jdk-slim

COPY . /app

WORKDIR /app

RUN chmod +x mvnw
RUN ./mvnw clean install -DskipTests

CMD ["java", "-jar", "target/mifinca-0.0.1-SNAPSHOT.jar"]

EXPOSE 9091