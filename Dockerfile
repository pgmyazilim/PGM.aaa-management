FROM eclipse-temurin:25-jre
ENV TZ=Europe/Nicosia
WORKDIR /app
COPY build/libs/*.jar /app/
RUN set -eux; \
    jar="$(find /app -maxdepth 1 -type f -name '*.jar' ! -name '*-plain.jar' | head -n1)"; \
    if [ -z "$jar" ]; then jar="$(find /app -maxdepth 1 -type f -name '*.jar' | head -n1)"; fi; \
    [ -n "$jar" ]; \
    mv "$jar" /app/app.jar; \
    find /app -maxdepth 1 -type f -name '*.jar' ! -name 'app.jar' -delete
EXPOSE 8080 8443
CMD ["java","-jar","app.jar"]