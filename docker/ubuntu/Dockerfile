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
CMD chmod +x mvnw && dos2unix mvnw && tail -f /dev/null 