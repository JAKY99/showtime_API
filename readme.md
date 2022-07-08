# prérequis 


## lancement de l'application 

- docker-compose up -d 
### recompiler 
Attention utiliser le terminal de votre os wsl-2 
le && sous le terminal windows ne fonctionne pas 
en revanche on peut enchainée les deux en remplaçant <&&> par <;> .

terminal linux sous windows =>
docker exec -it ubuntu-vm-s-time ./mvnw package && docker exec -it ubuntu-vm-s-time java -jar target/yak-0.0.1-SNAPSHOT.jar

terminal windows =>
docker exec -it ubuntu-vm-s-time ./mvnw package;docker exec -it ubuntu-vm-s-time java -jar target/yak-0.0.1-SNAPSHOT.jar 