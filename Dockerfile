FROM eclipse-temurin:11-jre-alpine

# Add Maintainer Info
LABEL maintainer="gabriele.deluca@eng.it"

RUN apk add --no-cache openssl curl

RUN mkdir -p /home/nobody/app && mkdir -p /home/nobody/data/log/ucapp && mkdir -p /.sigstore
RUN apk add --no-cache openssl curl cosign

WORKDIR /home/nobody

# The application's jar file
COPY target/dependency-jars /home/nobody/app/dependency-jars

# Add the application's jar to the container
ADD target/dataUsage.jar /home/nobody/app/dataUsage.jar

RUN chown -R nobody:nogroup /home/nobody && chown -R nobody:nogroup /.sigstore

USER 65534

ENTRYPOINT java -jar /home/nobody/app/dataUsage.jar
HEALTHCHECK --interval=5s --retries=12 --timeout=10s CMD curl --fail -k https://localhost:8080/platoontec/PlatoonDataUsage/1.0/about/version || exit 1