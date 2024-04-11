FROM sbtscala/scala-sbt:eclipse-temurin-jammy-17.0.10_7_1.9.9_3.3.3

RUN apt-get update

EXPOSE 8081

WORKDIR /persistence

ADD . /persistence

CMD sbt run