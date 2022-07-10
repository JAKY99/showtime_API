FROM ubuntu:20.04
RUN apt update -y
RUN apt upgrade -y
RUN apt-get install -y openjdk-17-jre
#242mo à télécharger donc ça prends du temps à installer
RUN apt-get install -y openjdk-17-jdk
RUN apt install maven -y
RUN apt-get update && apt-get install dos2unix -y
RUN apt install wget -y && apt install unzip -y && apt install vim -y
WORKDIR /root/project
COPY ubuntu.sh /root/project/ubuntu.sh
RUN chmod +x /root/project/ubuntu.sh
COPY showtime_API /root/project 
RUN rm /root/project/src/main/resources/application.properties
COPY application.properties  /root/project/src/main/resources/application.properties
COPY .mvn /root/project/.mvn
CMD chmod +x mvnw && dos2unix mvnw  && ./mvnw clean install spring-boot:repackage && ./mvnw spring-boot:run