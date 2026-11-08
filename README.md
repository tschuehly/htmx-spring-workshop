# HTMX + Spring Boot Workshop

You can find the workshop documentation at:

[tschuehly.gitbook.io/server-side-spring-htmx-workshop/building-server-side-web-applications-with-htmx](https://tschuehly.gitbook.io/server-side-spring-htmx-workshop/building-server-side-web-applications-with-htmx)

### Spring I/O 2024 (2h)

[2024.springio.net/sessions/building-server-side-web-applications-with-htmx-workshop/](https://2024.springio.net/sessions/building-server-side-web-applications-with-htmx-workshop/)

### Native image

`lab-completed` (Spring Boot 4) builds as a GraalVM native image: `docker build -t htmx-spring-workshop lab-completed`.
`.github/workflows/native-image.yml` builds and smoke-tests it (`lab-completed/smoke-test.sh`) and pushes
`ghcr.io/tschuehly/htmx-spring-workshop:<sha>` and `:latest` from the default branch.
