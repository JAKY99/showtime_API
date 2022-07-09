# lancement de l'application via intellij

- Une fois avoir récupéré le projet en entier rendez-vous dans docker/ubuntu/
- ouvrir le dossier showtime_API en tant que projet intellij

# prérequis : vérification du setup 

- bouton pour accéder au menu : 
![](https://github.com/achot-barseghyan/showtime_API/blob/devops2/readme-img/button_check_setting.png) 

- dans le menu choisissez : 
![](https://github.com/achot-barseghyan/showtime_API/blob/devops2/readme-img/menu_choice_check.png)

- vérifier le setup java du projet il doit être config de la manière suivante : 
![](https://github.com/achot-barseghyan/showtime_API/blob/devops2/readme-img/check_project_setting.png)

# premier lancement et config du launcher docker

- lancer l'application une première fois en automatique via intellij afin qu'il récupère toutes les dépendances

- pour ce faire rendez-vous dans src/main/java/com.m2i.showtime.yak/ShowTimeApplication.java 
![](https://github.com/achot-barseghyan/showtime_API/blob/devops2/readme-img/file_start_location_intellij.png)

- sur la ligne 19 vous devriez avoir une icone (play) en vert cliquer dessus
![](https://github.com/achot-barseghyan/showtime_API/blob/devops2/readme-img/first_run_intellij.png)


- l'application devrais se lancer et telecharger toutes les dépendances
![](https://github.com/achot-barseghyan/showtime_API/blob/devops2/readme-img/spring_launch_terminal_at_first_run.png)

- ensuite vous devriez avoir une première version du launcher de base disponible dans intellij : 
![](https://github.com/achot-barseghyan/showtime_API/blob/devops2/readme-img/registered_launch_app_method.png)

- maintenant créez la config pour docker qui vas lancer au préalable la commande initial que nous venons créer

- pour ce faire rendez vous dans la rubrique edit config : 
![](https://github.com/achot-barseghyan/showtime_API/blob/devops2/readme-img/edit_place_launch.png)

- cliquer sur add config : 
![](https://github.com/achot-barseghyan/showtime_API/blob/devops2/readme-img/add_new_config_for_docker.png)

- dans le menu cliquer sur docker puis choissisez docker-compose : 
![](https://github.com/achot-barseghyan/showtime_API/blob/devops2/readme-img/choice_docker_compose_for_new_config.png)

- copier la config en remplaçant le chemin absolue du fichier composer.yml par le votre : 
![](https://github.com/achot-barseghyan/showtime_API/blob/devops2/readme-img/copy_following_config.png)

- utiliser désormais le nouveau run que vous avez crée qui lui même lance avant le run de base : 
![](https://github.com/achot-barseghyan/showtime_API/blob/devops2/readme-img/use_docker_run_that_run_cascade_showtimeapplication.png)

 


## En cas de problème (container lié a la db)

=>etape 1
- supprimer les dossier local_pgdata et pgadmin-data
- docker ps , faite un docker-compose down (si les containers sont up) puis un docker-compose up -d --build --remove-orphans

=>etape 2 

si votre problème persiste faite docker ps , docker-compose down (si les containers sont up) , 
docker-compose build --no-cache puis docker-compose up -d

=>etape 3 - hard reset

- docker system prune -a (attention cette commande supprime tout vos containers,volumes,images inactifs ) puis reproduire l'étape 2