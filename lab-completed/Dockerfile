# GraalVM native image of the lab-completed app (Spring Boot 4 requires GraalVM 25+).
FROM ghcr.io/graalvm/native-image-community:25 AS build
RUN microdnf install -y findutils && microdnf clean all
WORKDIR /app
COPY . .
RUN ./gradlew nativeCompile --no-daemon

FROM debian:trixie-slim
LABEL org.opencontainers.image.source=https://github.com/tschuehly/htmx-spring-workshop
# Kamal requires service=<service> (deploy/apps/htmx-spring-workshop/config/deploy.yml).
LABEL service="htmx-spring-workshop"
COPY --from=build /app/build/native/nativeCompile/lab-completed /app/lab-completed
EXPOSE 8080
USER 1000
ENTRYPOINT ["/app/lab-completed"]
