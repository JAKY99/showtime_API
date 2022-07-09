# lancement de l'application via intellij

- Une fois avoir récupéré le projet en entier rendez-vous dans docker/ubuntu/
- ouvrir le dossier showtime_API en tant que projet intellij
- lancer l'application une première fois en automatique via intellij afin qu'il récupère toutes les dépendances 
- pour ce faire rendez-vous dans src/main/java/com.m2i.showtime.yak/ShowTimeApplication.java 
- sur la ligne 19 vous devriez avoir une icone (play) en vert cliquer dessus 
- l'application devrais se lancer et telecharger toutes les dépendances
- 


## En cas de problème (container lié a la db)

=>etape 1
- supprimer les dossier local_pgdata et pgadmin-data
- docker ps , faite un docker-compose down (si les containers sont up) puis un docker-compose up -d --build --remove-orphans
=>etape 2 - reset
- recloner le projet 
- si votre problème persiste faite docker ps , docker-compose down (si les containers sont up) , docker-compose build --no-cache puis docker-compose up -d

=>etape 3 - hard reset

- docker system prune -a (attention cette commande supprime tout vos containers,volumes,images inactifs ) puis reproduire l'étape 2