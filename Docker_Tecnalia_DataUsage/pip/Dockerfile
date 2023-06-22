FROM eclipse-temurin:11-jre-alpine

# Add Maintainer Info
LABEL maintainer="gabriele.deluca@eng.it"

WORKDIR tmp
COPY *.jar pip.jar
#COPY etc/pip.properties /etc/pip.properties
ENTRYPOINT ["java","-jar","pip.jar"]




