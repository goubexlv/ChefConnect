# Stage 1: Cache Gradle dependencies
FROM gradle:8.4-jdk17 AS cache
RUN mkdir -p /home/gradle/cache_home
ENV GRADLE_USER_HOME=/home/gradle/cache_home
WORKDIR /home/gradle/app
COPY build.gradle.* gradle.properties settings.gradle.kts gradle/ ./
RUN gradle dependencies --no-daemon || return 0

# Stage 2: Build Application
FROM gradle:8.4-jdk17 AS build
COPY --from=cache /home/gradle/cache_home /home/gradle/.gradle
WORKDIR /home/gradle/src
COPY . .
RUN gradle buildFatJar --no-daemon --stacktrace

# Stage 3: Runtime Image
FROM amazoncorretto:22 AS runtime
EXPOSE 8080
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/chefconnect.jar
ENTRYPOINT ["java", "-jar", "/app/chefconnect.jar"]
