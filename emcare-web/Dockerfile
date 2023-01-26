FROM maven:3.6.3-jdk-13 AS MAVEN_BUILD

MAINTAINER Brian Hannaway

COPY pom.xml /build/
COPY src /build/src/

WORKDIR /build/
RUN mvn package

RUN mv /build/target/*.jar /build/target/app.jar

FROM openjdk:8-jre-alpine

WORKDIR /app


ENV KEYCLOACK_SERVER_URL keycloak
ENV KEYCLOACK_CLIENT_SECRET mylittleSecret
ENV KEYCLOACK_CLIENT_ID myid
ENV KEYCLOACK_REALM emcare
ENV KEYCLOACK_USER_NAME emcare@gmail.com
ENV KEYCLOACK_PASSWORD argusadmin
ENV KEYCLOACK_MASTER_USER_ID emcare
ENV KEYCLOACK_MASTER_USER_PASSWORD arguseadmin
# TWILO 
ENV TWILLO_SSID myssid
ENV TWILLO_TOKEN mytoken
ENV TWILLO_SID mysid
ENV TWILLO_NUMBER 01234567890
# ENV ibm.access-key** -> Provide a key for dynamic language translation (You have to create an account in IBM and get the key from there https://www.ibm.com/cloud/watson-language-translator)
ENV MAIL_PASSWORD password
ENV MAIL_USER emcare@gmail.com
ENV MAIL_SERVER smpt.gmail.com
ENV MAIL_PORT 587
ENV DB_HOST postgresql
ENV DB_NAME emcare
ENV DB_PASSWORD argusadmin
ENV DB_USER emcare

ENV IBM_KEY myannoyingkey

COPY --from=MAVEN_BUILD /build/target/app.jar /app/

ENTRYPOINT ["java", "-jar", "app.jar"]

