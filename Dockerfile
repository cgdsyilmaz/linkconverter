FROM openjdk:15
MAINTAINER cagdasyilmaz
VOLUME /tmp
ADD build/libs/applicanttest-0.0.1-SNAPSHOT.jar link-converter.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/link-converter.jar"]