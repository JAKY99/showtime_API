FROM ubuntu:20.04
RUN apt update
RUN apt upgrade -y
RUN apt update -y  && apt-get install -y  openjdk-17-jre && apt-get install -y openjdk-17-jdk
RUN apt install maven -y
RUN apt install wget -y && apt install unzip -y && apt install vim -y
CMD tail -f /dev/null