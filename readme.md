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


#### En cas de problème (container lié a la db)

=>etape 1
- supprimer les dossier local_pgdata et pgadmin-data
- docker ps , faite un docker-compose down (si les containers sont up) puis un docker-compose up -d --build --remove-orphans
=>etape 2 - reset
- recloner le projet 
- si votre problème persiste faite docker ps , docker-compose down (si les containers sont up) , docker-compose build --no-cache puis docker-compose up -d

=>etape 3 - hard reset

- docker system prune -a (attention cette commande supprime tout vos containers,volumes,images inactifs ) puis reproduire l'étape 2