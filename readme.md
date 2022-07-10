# recuperation du projet en mode devops2
- crée un dossier vierge pour contenir le projet
- ourvrir ce dossier via un terminal (git ou window/powershell ou le terminal linux sous window)
- cloner le projet : git clone https://github.com/achot-barseghyan/showtime_API.git
- rentré dans le projet cloné : cd showtime_API 
- aller sur la bonne branche : git checkout devops2

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
- une demande d'autorisation pour lambok sera demandé la première fois cliquer sur enable une fois et ça suffira 
![](https://github.com/achot-barseghyan/showtime_API/blob/devops2/readme-img/enable-lambok.png)

- pour ce faire rendez-vous dans src/main/java/com.m2i.showtime.yak/ShowTimeApplication.java 
![](https://github.com/achot-barseghyan/showtime_API/blob/devops2/readme-img/file_start_location_intellij.png)

- sur la ligne 19 vous devriez avoir une icone (play) en vert cliquer dessus
![](https://github.com/achot-barseghyan/showtime_API/blob/devops2/readme-img/first_run_intellij.png)

- ensuite choisissez le premier choix de la liste :
![](https://github.com/achot-barseghyan/showtime_API/blob/devops2/readme-img/choice_first_run.png)

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

- dans la rubrique Modify de la ligne docker-compose up rajouter les option < Attach to: none > et < Recreate containers : all > :
![](https://github.com/achot-barseghyan/showtime_API/blob/devops2/readme-img/add_compose_option.png)

- dans la rubrique Before launch faite un clique sur le boutton + et faite un click sur run maven goal :
![](https://github.com/achot-barseghyan/showtime_API/blob/devops2/readme-img/select_run_maven_goal_docker_run_config.png)

- dans le champs command line rajouter la commande < package -DskipTests  > puis ok 
![](https://github.com/achot-barseghyan/showtime_API/blob/devops2/readme-img/add_command_line_maven.png)

- faite ok dans le fenetre configuration pour la valider 

- vous pouvez désormais lancer docker run 
 
- patientez environ 30s max le temps que l'application soit disponible sur le localhost:89


## En cas de problème (container lié a la db)

=>etape 1
- supprimer les dossier local_pgdata et pgadmin-data
- docker ps , faite un docker-compose down (si les containers sont up) puis un docker-compose up -d --build --remove-orphans

=>etape 2 

si votre problème persiste faite docker ps , docker-compose down (si les containers sont up) , 
docker-compose build --no-cache puis docker-compose up -d

=>etape 3 - hard reset

- docker system prune -a (attention cette commande supprime tout vos containers,volumes,images inactifs ) puis reproduire l'étape 2


### en cas de problème dans intellij suite à pull fermé et réouvrir l'IDE