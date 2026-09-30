FROM tomcat:10.1.60-jdk25-temurin-noble

RUN rm -rf /usr/local/tomcat/webapps/*

COPY dist/POOII.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]