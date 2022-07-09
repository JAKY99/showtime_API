FROM ubuntu:20.04
RUN apt update -y
RUN apt upgrade -y
RUN apt-get install -y openjdk-17-jre
#242mo à télécharger donc ça prends du temps à installer
RUN apt-get install -y openjdk-17-jdk
RUN apt install maven -y
RUN apt-get update && apt-get install dos2unix -y
RUN apt install wget -y && apt install unzip -y && apt install vim -y
#ici est la correction du problème rentré lors de l'execution de la commande .mvnw problème CTRL/LFet droit d'exécution
#le tail -f /dev/null permet de maintenir le container en cours d'exécution 
#kill $(ps aux | grep 'yak-0.0.1-SNAPSHOT.jar' | grep -v grep | awk '{print $2}')
COPY ubuntu.sh /root/ubuntu.sh
RUN chmod +x /root/ubuntu.sh
# CMD chmod +x mvnw && dos2unix mvnw  && ./mvnw package && java -jar target/yak-0.0.1-SNAPSHOT.jar && tail -f /dev/null
COPY showtime_API /root/project
RUN rm /root/project/src/main/resources/application.properties
COPY application.properties  /root/project/src/main/resources/application.properties
COPY .mvn /root/project/.mvn
COPY ../test.txt /root/project/src/test/resources/test.txt

ENTRYPOINT ["/bin/bash", "/root/ubuntu.sh"]