FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

COPY .mvn .mvn
COPY mvnw pom.xml ./
COPY src src

RUN ./mvnw -DskipTests package

FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=build /app/target/product-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
