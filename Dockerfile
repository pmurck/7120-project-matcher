FROM gradle:latest as builder
COPY settings.gradle gradle.properties build.gradle ./
COPY resources ./resources
COPY src ./src
RUN gradle installDist

FROM openjdk:11-jdk
EXPOSE 8081:8081
RUN mkdir /app
COPY --from=builder /home/gradle/build/install/project-matcher/ /app/
WORKDIR /app/bin
CMD ["./project-matcher"]

