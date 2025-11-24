# STAGE 1 – Build
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# copy code
COPY . .

# đảm bảo mvnw executable
RUN chmod +x mvnw

# build bằng maven wrapper (không dùng maven system)
RUN ./mvnw -q clean package -DskipTests

# STAGE 2 – Runtime
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
