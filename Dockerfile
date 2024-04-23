FROM maven:3.9.6 as builder

WORKDIR /build

COPY ./pom.xml ./pom.xml
COPY ./ ./

RUN mvn package

FROM tomcat:9.0-jdk17

RUN rm -rf /usr/local/tomcat/webapps/*

COPY --from=builder /build/shacl-play/target/*.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]
