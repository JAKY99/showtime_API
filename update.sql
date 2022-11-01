
    alter table _user 
       drop constraint FK7wki6bxsew9feo0m49h59hiec;

    alter table role_permissions 
       drop constraint FKh0v7u4w7mttcu81o8wegayr8e;

    alter table role_permissions 
       drop constraint FKlodb7xh4a2xjv39gc3lsop95n;

    alter table season_episodes 
       drop constraint FKsxx2395f319y29g2l3kkmpg7t;

    alter table season_episodes 
       drop constraint FKkye079mxw7jbjgfb4s01y1exa;

    alter table users_watched_movies 
       drop constraint FKlypo8hs1hkiaiqcrv4y2q5kgp;

    alter table users_watched_movies 
       drop constraint FKiul12g2u3b8m4xg6fje90wer8;

    alter table users_watched_series 
       drop constraint FKpudtag2nf22r6x8eyegwn6rd0;

    alter table users_watched_series 
       drop constraint FK8dt3byf2l15ilneartbj5ilta;

    drop table if exists _user cascade;

    drop table if exists episode cascade;

    drop table if exists movie cascade;

    drop table if exists permission cascade;

    drop table if exists role cascade;

    drop table if exists role_permissions cascade;

    drop table if exists season cascade;

    drop table if exists season_episodes cascade;

    drop table if exists serie cascade;

    drop table if exists users_watched_movies cascade;

    drop table if exists users_watched_series cascade;

    drop sequence if exists permission_sequence;

    drop sequence if exists role_sequence;

    drop sequence if exists user_sequence;
create sequence permission_sequence start 1 increment 1;
create sequence role_sequence start 1 increment 1;
create sequence user_sequence start 1 increment 1;

    create table _user (
       id int8 not null,
        background_picture varchar(255),
        country varchar(255),
        date_created timestamp,
        first_name varchar(255),
        is_account_non_expired boolean,
        is_account_non_locked boolean,
        is_account_private boolean,
        is_credentials_non_expired boolean,
        is_deleted boolean,
        is_enabled boolean,
        is_notifications_active boolean,
        is_notifications_comments_active boolean,
        is_notifications_trophies_active boolean,
        last_name varchar(255),
        _password varchar(255),
        profile_picture varchar(64),
        total_episodes_watched_number int8,
        total_movie_watched_number int8,
        total_movie_watched_time int8,
        total_series_watched_number int8,
        total_series_watched_time int8,
        email varchar(255),
        role_id int8,
        primary key (id)
    );

    create table episode (
       id int8 not null,
        imbd_id int8,
        name varchar(255),
        watched boolean,
        primary key (id)
    );

    create table movie (
       id int8 not null,
        name varchar(255),
        primary key (id)
    );

    create table permission (
       id int8 not null,
        description varchar(255),
        display_name varchar(255),
        permission varchar(255),
        primary key (id)
    );

    create table role (
       id int8 not null,
        description varchar(255),
        description10 varchar(255),
        display_name varchar(255),
        role varchar(255),
        primary key (id)
    );

    create table role_permissions (
       role_id int8 not null,
        permission_id int8 not null,
        primary key (role_id, permission_id)
    );

    create table season (
       id int8 not null,
        name varchar(255),
        season_number varchar(255),
        primary key (id)
    );

    create table season_episodes (
       season_id int8 not null,
        episodes_id int8 not null
    );

    create table serie (
       id int8 not null,
        imdb_id int8,
        name varchar(255),
        primary key (id)
    );

    create table users_watched_movies (
       user_id int8 not null,
        movie_id int8 not null,
        primary key (user_id, movie_id)
    );

    create table users_watched_series (
       user_id int8 not null,
        serie_id int8 not null,
        primary key (user_id, serie_id)
    );

    alter table _user 
       add constraint UK_k11y3pdtsrjgy8w9b6q4bjwrx unique (email);

    alter table permission 
       add constraint UK_9kwkevw5na26e6qb4cbcbxaa4 unique (permission);

    alter table role 
       add constraint UK_bjxn5ii7v7ygwx39et0wawu0q unique (role);

    alter table season_episodes 
       add constraint UK_3n2w856u2hpxhgkuo5o46oiof unique (episodes_id);

    alter table _user 
       add constraint FK7wki6bxsew9feo0m49h59hiec 
       foreign key (role_id) 
       references role;

    alter table role_permissions 
       add constraint FKh0v7u4w7mttcu81o8wegayr8e 
       foreign key (permission_id) 
       references permission;

    alter table role_permissions 
       add constraint FKlodb7xh4a2xjv39gc3lsop95n 
       foreign key (role_id) 
       references role;

    alter table season_episodes 
       add constraint FKsxx2395f319y29g2l3kkmpg7t 
       foreign key (episodes_id) 
       references episode;

    alter table season_episodes 
       add constraint FKkye079mxw7jbjgfb4s01y1exa 
       foreign key (season_id) 
       references season;

    alter table users_watched_movies 
       add constraint FKlypo8hs1hkiaiqcrv4y2q5kgp 
       foreign key (movie_id) 
       references movie;

    alter table users_watched_movies 
       add constraint FKiul12g2u3b8m4xg6fje90wer8 
       foreign key (user_id) 
       references _user;

    alter table users_watched_series 
       add constraint FKpudtag2nf22r6x8eyegwn6rd0 
       foreign key (serie_id) 
       references serie;

    alter table users_watched_series 
       add constraint FK8dt3byf2l15ilneartbj5ilta 
       foreign key (user_id) 
       references _user;

    alter table _user 
       drop constraint FK7wki6bxsew9feo0m49h59hiec;

    alter table role_permissions 
       drop constraint FKh0v7u4w7mttcu81o8wegayr8e;

    alter table role_permissions 
       drop constraint FKlodb7xh4a2xjv39gc3lsop95n;

    alter table season_episodes 
       drop constraint FKsxx2395f319y29g2l3kkmpg7t;

    alter table season_episodes 
       drop constraint FKkye079mxw7jbjgfb4s01y1exa;

    alter table users_watched_movies 
       drop constraint FKlypo8hs1hkiaiqcrv4y2q5kgp;

    alter table users_watched_movies 
       drop constraint FKiul12g2u3b8m4xg6fje90wer8;

    alter table users_watched_series 
       drop constraint FKpudtag2nf22r6x8eyegwn6rd0;

    alter table users_watched_series 
       drop constraint FK8dt3byf2l15ilneartbj5ilta;

    drop table if exists _user cascade;

    drop table if exists episode cascade;

    drop table if exists movie cascade;

    drop table if exists permission cascade;

    drop table if exists role cascade;

    drop table if exists role_permissions cascade;

    drop table if exists season cascade;

    drop table if exists season_episodes cascade;

    drop table if exists serie cascade;

    drop table if exists users_watched_movies cascade;

    drop table if exists users_watched_series cascade;

    drop sequence if exists permission_sequence;

    drop sequence if exists role_sequence;

    drop sequence if exists user_sequence;
create sequence permission_sequence start 1 increment 1;
create sequence role_sequence start 1 increment 1;
create sequence user_sequence start 1 increment 1;

    create table _user (
       id int8 not null,
        background_picture varchar(255),
        country varchar(255),
        date_created timestamp,
        first_name varchar(255),
        is_account_non_expired boolean,
        is_account_non_locked boolean,
        is_account_private boolean,
        is_credentials_non_expired boolean,
        is_deleted boolean,
        is_enabled boolean,
        is_notifications_active boolean,
        is_notifications_comments_active boolean,
        is_notifications_trophies_active boolean,
        last_name varchar(255),
        _password varchar(255),
        profile_picture varchar(64),
        total_episodes_watched_number int8,
        total_movie_watched_number int8,
        total_movie_watched_time int8,
        total_series_watched_number int8,
        total_series_watched_time int8,
        email varchar(255),
        role_id int8,
        primary key (id)
    );

    create table episode (
       id int8 not null,
        imbd_id int8,
        name varchar(255),
        watched boolean,
        primary key (id)
    );

    create table movie (
       id int8 not null,
        name varchar(255),
        primary key (id)
    );

    create table permission (
       id int8 not null,
        description varchar(255),
        display_name varchar(255),
        permission varchar(255),
        primary key (id)
    );

    create table role (
       id int8 not null,
        description varchar(255),
        description10 varchar(255),
        display_name varchar(255),
        role varchar(255),
        primary key (id)
    );

    create table role_permissions (
       role_id int8 not null,
        permission_id int8 not null,
        primary key (role_id, permission_id)
    );

    create table season (
       id int8 not null,
        name varchar(255),
        season_number varchar(255),
        primary key (id)
    );

    create table season_episodes (
       season_id int8 not null,
        episodes_id int8 not null
    );

    create table serie (
       id int8 not null,
        imdb_id int8,
        name varchar(255),
        primary key (id)
    );

    create table users_watched_movies (
       user_id int8 not null,
        movie_id int8 not null,
        primary key (user_id, movie_id)
    );

    create table users_watched_series (
       user_id int8 not null,
        serie_id int8 not null,
        primary key (user_id, serie_id)
    );

    alter table _user 
       add constraint UK_k11y3pdtsrjgy8w9b6q4bjwrx unique (email);

    alter table permission 
       add constraint UK_9kwkevw5na26e6qb4cbcbxaa4 unique (permission);

    alter table role 
       add constraint UK_bjxn5ii7v7ygwx39et0wawu0q unique (role);

    alter table season_episodes 
       add constraint UK_3n2w856u2hpxhgkuo5o46oiof unique (episodes_id);

    alter table _user 
       add constraint FK7wki6bxsew9feo0m49h59hiec 
       foreign key (role_id) 
       references role;

    alter table role_permissions 
       add constraint FKh0v7u4w7mttcu81o8wegayr8e 
       foreign key (permission_id) 
       references permission;

    alter table role_permissions 
       add constraint FKlodb7xh4a2xjv39gc3lsop95n 
       foreign key (role_id) 
       references role;

    alter table season_episodes 
       add constraint FKsxx2395f319y29g2l3kkmpg7t 
       foreign key (episodes_id) 
       references episode;

    alter table season_episodes 
       add constraint FKkye079mxw7jbjgfb4s01y1exa 
       foreign key (season_id) 
       references season;

    alter table users_watched_movies 
       add constraint FKlypo8hs1hkiaiqcrv4y2q5kgp 
       foreign key (movie_id) 
       references movie;

    alter table users_watched_movies 
       add constraint FKiul12g2u3b8m4xg6fje90wer8 
       foreign key (user_id) 
       references _user;

    alter table users_watched_series 
       add constraint FKpudtag2nf22r6x8eyegwn6rd0 
       foreign key (serie_id) 
       references serie;

    alter table users_watched_series 
       add constraint FK8dt3byf2l15ilneartbj5ilta 
       foreign key (user_id) 
       references _user;

    alter table _user 
       drop constraint FK7wki6bxsew9feo0m49h59hiec;

    alter table role_permissions 
       drop constraint FKh0v7u4w7mttcu81o8wegayr8e;

    alter table role_permissions 
       drop constraint FKlodb7xh4a2xjv39gc3lsop95n;

    alter table season_episodes 
       drop constraint FKsxx2395f319y29g2l3kkmpg7t;

    alter table season_episodes 
       drop constraint FKkye079mxw7jbjgfb4s01y1exa;

    alter table users_watched_movies 
       drop constraint FKlypo8hs1hkiaiqcrv4y2q5kgp;

    alter table users_watched_movies 
       drop constraint FKiul12g2u3b8m4xg6fje90wer8;

    alter table users_watched_series 
       drop constraint FKpudtag2nf22r6x8eyegwn6rd0;

    alter table users_watched_series 
       drop constraint FK8dt3byf2l15ilneartbj5ilta;

    drop table if exists _user cascade;

    drop table if exists episode cascade;

    drop table if exists movie cascade;

    drop table if exists permission cascade;

    drop table if exists role cascade;

    drop table if exists role_permissions cascade;

    drop table if exists season cascade;

    drop table if exists season_episodes cascade;

    drop table if exists serie cascade;

    drop table if exists users_watched_movies cascade;

    drop table if exists users_watched_series cascade;

    drop sequence if exists permission_sequence;

    drop sequence if exists role_sequence;

    drop sequence if exists user_sequence;
create sequence permission_sequence start 1 increment 1;
create sequence role_sequence start 1 increment 1;
create sequence user_sequence start 1 increment 1;

    create table _user (
       id int8 not null,
        background_picture varchar(255),
        country varchar(255),
        date_created timestamp,
        first_name varchar(255),
        is_account_non_expired boolean,
        is_account_non_locked boolean,
        is_account_private boolean,
        is_credentials_non_expired boolean,
        is_deleted boolean,
        is_enabled boolean,
        is_notifications_active boolean,
        is_notifications_comments_active boolean,
        is_notifications_trophies_active boolean,
        last_name varchar(255),
        _password varchar(255),
        profile_picture varchar(64),
        total_episodes_watched_number int8,
        total_movie_watched_number int8,
        total_movie_watched_time int8,
        total_series_watched_number int8,
        total_series_watched_time int8,
        email varchar(255),
        role_id int8,
        primary key (id)
    );

    create table episode (
       id int8 not null,
        imbd_id int8,
        name varchar(255),
        watched boolean,
        primary key (id)
    );

    create table movie (
       id int8 not null,
        name varchar(255),
        primary key (id)
    );

    create table permission (
       id int8 not null,
        description varchar(255),
        display_name varchar(255),
        permission varchar(255),
        primary key (id)
    );

    create table role (
       id int8 not null,
        description varchar(255),
        description10 varchar(255),
        display_name varchar(255),
        role varchar(255),
        primary key (id)
    );

    create table role_permissions (
       role_id int8 not null,
        permission_id int8 not null,
        primary key (role_id, permission_id)
    );

    create table season (
       id int8 not null,
        name varchar(255),
        season_number varchar(255),
        primary key (id)
    );

    create table season_episodes (
       season_id int8 not null,
        episodes_id int8 not null
    );

    create table serie (
       id int8 not null,
        imdb_id int8,
        name varchar(255),
        primary key (id)
    );

    create table users_watched_movies (
       user_id int8 not null,
        movie_id int8 not null,
        primary key (user_id, movie_id)
    );

    create table users_watched_series (
       user_id int8 not null,
        serie_id int8 not null,
        primary key (user_id, serie_id)
    );

    alter table _user 
       add constraint UK_k11y3pdtsrjgy8w9b6q4bjwrx unique (email);

    alter table permission 
       add constraint UK_9kwkevw5na26e6qb4cbcbxaa4 unique (permission);

    alter table role 
       add constraint UK_bjxn5ii7v7ygwx39et0wawu0q unique (role);

    alter table season_episodes 
       add constraint UK_3n2w856u2hpxhgkuo5o46oiof unique (episodes_id);

    alter table _user 
       add constraint FK7wki6bxsew9feo0m49h59hiec 
       foreign key (role_id) 
       references role;

    alter table role_permissions 
       add constraint FKh0v7u4w7mttcu81o8wegayr8e 
       foreign key (permission_id) 
       references permission;

    alter table role_permissions 
       add constraint FKlodb7xh4a2xjv39gc3lsop95n 
       foreign key (role_id) 
       references role;

    alter table season_episodes 
       add constraint FKsxx2395f319y29g2l3kkmpg7t 
       foreign key (episodes_id) 
       references episode;

    alter table season_episodes 
       add constraint FKkye079mxw7jbjgfb4s01y1exa 
       foreign key (season_id) 
       references season;

    alter table users_watched_movies 
       add constraint FKlypo8hs1hkiaiqcrv4y2q5kgp 
       foreign key (movie_id) 
       references movie;

    alter table users_watched_movies 
       add constraint FKiul12g2u3b8m4xg6fje90wer8 
       foreign key (user_id) 
       references _user;

    alter table users_watched_series 
       add constraint FKpudtag2nf22r6x8eyegwn6rd0 
       foreign key (serie_id) 
       references serie;

    alter table users_watched_series 
       add constraint FK8dt3byf2l15ilneartbj5ilta 
       foreign key (user_id) 
       references _user;

    alter table _user 
       drop constraint FK7wki6bxsew9feo0m49h59hiec;

    alter table role_permissions 
       drop constraint FKh0v7u4w7mttcu81o8wegayr8e;

    alter table role_permissions 
       drop constraint FKlodb7xh4a2xjv39gc3lsop95n;

    alter table season_episodes 
       drop constraint FKsxx2395f319y29g2l3kkmpg7t;

    alter table season_episodes 
       drop constraint FKkye079mxw7jbjgfb4s01y1exa;

    alter table users_watched_movies 
       drop constraint FKlypo8hs1hkiaiqcrv4y2q5kgp;

    alter table users_watched_movies 
       drop constraint FKiul12g2u3b8m4xg6fje90wer8;

    alter table users_watched_series 
       drop constraint FKpudtag2nf22r6x8eyegwn6rd0;

    alter table users_watched_series 
       drop constraint FK8dt3byf2l15ilneartbj5ilta;

    drop table if exists _user cascade;

    drop table if exists episode cascade;

    drop table if exists movie cascade;

    drop table if exists permission cascade;

    drop table if exists role cascade;

    drop table if exists role_permissions cascade;

    drop table if exists season cascade;

    drop table if exists season_episodes cascade;

    drop table if exists serie cascade;

    drop table if exists users_watched_movies cascade;

    drop table if exists users_watched_series cascade;

    drop sequence if exists permission_sequence;

    drop sequence if exists role_sequence;

    drop sequence if exists user_sequence;
create sequence permission_sequence start 1 increment 1;
create sequence role_sequence start 1 increment 1;
create sequence user_sequence start 1 increment 1;

    create table _user (
       id int8 not null,
        background_picture varchar(255),
        country varchar(255),
        date_created timestamp,
        first_name varchar(255),
        is_account_non_expired boolean,
        is_account_non_locked boolean,
        is_account_private boolean,
        is_credentials_non_expired boolean,
        is_deleted boolean,
        is_enabled boolean,
        is_notifications_active boolean,
        is_notifications_comments_active boolean,
        is_notifications_trophies_active boolean,
        last_name varchar(255),
        _password varchar(255),
        profile_picture varchar(64),
        total_episodes_watched_number int8,
        total_movie_watched_number int8,
        total_movie_watched_time int8,
        total_series_watched_number int8,
        total_series_watched_time int8,
        email varchar(255),
        role_id int8,
        primary key (id)
    );

    create table episode (
       id int8 not null,
        imbd_id int8,
        name varchar(255),
        watched boolean,
        primary key (id)
    );

    create table movie (
       id int8 not null,
        name varchar(255),
        primary key (id)
    );

    create table permission (
       id int8 not null,
        description varchar(255),
        display_name varchar(255),
        permission varchar(255),
        primary key (id)
    );

    create table role (
       id int8 not null,
        description varchar(255),
        description10 varchar(255),
        display_name varchar(255),
        role varchar(255),
        primary key (id)
    );

    create table role_permissions (
       role_id int8 not null,
        permission_id int8 not null,
        primary key (role_id, permission_id)
    );

    create table season (
       id int8 not null,
        name varchar(255),
        season_number varchar(255),
        primary key (id)
    );

    create table season_episodes (
       season_id int8 not null,
        episodes_id int8 not null
    );

    create table serie (
       id int8 not null,
        imdb_id int8,
        name varchar(255),
        primary key (id)
    );

    create table users_watched_movies (
       user_id int8 not null,
        movie_id int8 not null,
        primary key (user_id, movie_id)
    );

    create table users_watched_series (
       user_id int8 not null,
        serie_id int8 not null,
        primary key (user_id, serie_id)
    );

    alter table _user 
       add constraint UK_k11y3pdtsrjgy8w9b6q4bjwrx unique (email);

    alter table permission 
       add constraint UK_9kwkevw5na26e6qb4cbcbxaa4 unique (permission);

    alter table role 
       add constraint UK_bjxn5ii7v7ygwx39et0wawu0q unique (role);

    alter table season_episodes 
       add constraint UK_3n2w856u2hpxhgkuo5o46oiof unique (episodes_id);

    alter table _user 
       add constraint FK7wki6bxsew9feo0m49h59hiec 
       foreign key (role_id) 
       references role;

    alter table role_permissions 
       add constraint FKh0v7u4w7mttcu81o8wegayr8e 
       foreign key (permission_id) 
       references permission;

    alter table role_permissions 
       add constraint FKlodb7xh4a2xjv39gc3lsop95n 
       foreign key (role_id) 
       references role;

    alter table season_episodes 
       add constraint FKsxx2395f319y29g2l3kkmpg7t 
       foreign key (episodes_id) 
       references episode;

    alter table season_episodes 
       add constraint FKkye079mxw7jbjgfb4s01y1exa 
       foreign key (season_id) 
       references season;

    alter table users_watched_movies 
       add constraint FKlypo8hs1hkiaiqcrv4y2q5kgp 
       foreign key (movie_id) 
       references movie;

    alter table users_watched_movies 
       add constraint FKiul12g2u3b8m4xg6fje90wer8 
       foreign key (user_id) 
       references _user;

    alter table users_watched_series 
       add constraint FKpudtag2nf22r6x8eyegwn6rd0 
       foreign key (serie_id) 
       references serie;

    alter table users_watched_series 
       add constraint FK8dt3byf2l15ilneartbj5ilta 
       foreign key (user_id) 
       references _user;

    alter table _user 
       drop constraint FK7wki6bxsew9feo0m49h59hiec;

    alter table role_permissions 
       drop constraint FKh0v7u4w7mttcu81o8wegayr8e;

    alter table role_permissions 
       drop constraint FKlodb7xh4a2xjv39gc3lsop95n;

    alter table season_episodes 
       drop constraint FKsxx2395f319y29g2l3kkmpg7t;

    alter table season_episodes 
       drop constraint FKkye079mxw7jbjgfb4s01y1exa;

    alter table users_watched_movies 
       drop constraint FKlypo8hs1hkiaiqcrv4y2q5kgp;

    alter table users_watched_movies 
       drop constraint FKiul12g2u3b8m4xg6fje90wer8;

    alter table users_watched_series 
       drop constraint FKpudtag2nf22r6x8eyegwn6rd0;

    alter table users_watched_series 
       drop constraint FK8dt3byf2l15ilneartbj5ilta;

    drop table if exists _user cascade;

    drop table if exists episode cascade;

    drop table if exists movie cascade;

    drop table if exists permission cascade;

    drop table if exists role cascade;

    drop table if exists role_permissions cascade;

    drop table if exists season cascade;

    drop table if exists season_episodes cascade;

    drop table if exists serie cascade;

    drop table if exists users_watched_movies cascade;

    drop table if exists users_watched_series cascade;

    drop sequence if exists permission_sequence;

    drop sequence if exists role_sequence;

    drop sequence if exists user_sequence;
create sequence permission_sequence start 1 increment 1;
create sequence role_sequence start 1 increment 1;
create sequence user_sequence start 1 increment 1;

    create table _user (
       id int8 not null,
        background_picture varchar(255),
        country varchar(255),
        date_created timestamp,
        first_name varchar(255),
        is_account_non_expired boolean,
        is_account_non_locked boolean,
        is_account_private boolean,
        is_credentials_non_expired boolean,
        is_deleted boolean,
        is_enabled boolean,
        is_notifications_active boolean,
        is_notifications_comments_active boolean,
        is_notifications_trophies_active boolean,
        last_name varchar(255),
        _password varchar(255),
        profile_picture varchar(64),
        total_episodes_watched_number int8,
        total_movie_watched_number int8,
        total_movie_watched_time int8,
        total_series_watched_number int8,
        total_series_watched_time int8,
        email varchar(255),
        role_id int8,
        primary key (id)
    );

    create table episode (
       id int8 not null,
        imbd_id int8,
        name varchar(255),
        watched boolean,
        primary key (id)
    );

    create table movie (
       id int8 not null,
        name varchar(255),
        primary key (id)
    );

    create table permission (
       id int8 not null,
        description varchar(255),
        display_name varchar(255),
        permission varchar(255),
        primary key (id)
    );

    create table role (
       id int8 not null,
        description varchar(255),
        description10 varchar(255),
        display_name varchar(255),
        role varchar(255),
        primary key (id)
    );

    create table role_permissions (
       role_id int8 not null,
        permission_id int8 not null,
        primary key (role_id, permission_id)
    );

    create table season (
       id int8 not null,
        name varchar(255),
        season_number varchar(255),
        primary key (id)
    );

    create table season_episodes (
       season_id int8 not null,
        episodes_id int8 not null
    );

    create table serie (
       id int8 not null,
        imdb_id int8,
        name varchar(255),
        primary key (id)
    );

    create table users_watched_movies (
       user_id int8 not null,
        movie_id int8 not null,
        primary key (user_id, movie_id)
    );

    create table users_watched_series (
       user_id int8 not null,
        serie_id int8 not null,
        primary key (user_id, serie_id)
    );

    alter table _user 
       add constraint UK_k11y3pdtsrjgy8w9b6q4bjwrx unique (email);

    alter table permission 
       add constraint UK_9kwkevw5na26e6qb4cbcbxaa4 unique (permission);

    alter table role 
       add constraint UK_bjxn5ii7v7ygwx39et0wawu0q unique (role);

    alter table season_episodes 
       add constraint UK_3n2w856u2hpxhgkuo5o46oiof unique (episodes_id);

    alter table _user 
       add constraint FK7wki6bxsew9feo0m49h59hiec 
       foreign key (role_id) 
       references role;

    alter table role_permissions 
       add constraint FKh0v7u4w7mttcu81o8wegayr8e 
       foreign key (permission_id) 
       references permission;

    alter table role_permissions 
       add constraint FKlodb7xh4a2xjv39gc3lsop95n 
       foreign key (role_id) 
       references role;

    alter table season_episodes 
       add constraint FKsxx2395f319y29g2l3kkmpg7t 
       foreign key (episodes_id) 
       references episode;

    alter table season_episodes 
       add constraint FKkye079mxw7jbjgfb4s01y1exa 
       foreign key (season_id) 
       references season;

    alter table users_watched_movies 
       add constraint FKlypo8hs1hkiaiqcrv4y2q5kgp 
       foreign key (movie_id) 
       references movie;

    alter table users_watched_movies 
       add constraint FKiul12g2u3b8m4xg6fje90wer8 
       foreign key (user_id) 
       references _user;

    alter table users_watched_series 
       add constraint FKpudtag2nf22r6x8eyegwn6rd0 
       foreign key (serie_id) 
       references serie;

    alter table users_watched_series 
       add constraint FK8dt3byf2l15ilneartbj5ilta 
       foreign key (user_id) 
       references _user;

    alter table _user 
       drop constraint FK7wki6bxsew9feo0m49h59hiec;

    alter table role_permissions 
       drop constraint FKh0v7u4w7mttcu81o8wegayr8e;

    alter table role_permissions 
       drop constraint FKlodb7xh4a2xjv39gc3lsop95n;

    alter table season_episodes 
       drop constraint FKsxx2395f319y29g2l3kkmpg7t;

    alter table season_episodes 
       drop constraint FKkye079mxw7jbjgfb4s01y1exa;

    alter table users_watched_movies 
       drop constraint FKlypo8hs1hkiaiqcrv4y2q5kgp;

    alter table users_watched_movies 
       drop constraint FKiul12g2u3b8m4xg6fje90wer8;

    alter table users_watched_series 
       drop constraint FKpudtag2nf22r6x8eyegwn6rd0;

    alter table users_watched_series 
       drop constraint FK8dt3byf2l15ilneartbj5ilta;

    drop table if exists _user cascade;

    drop table if exists episode cascade;

    drop table if exists movie cascade;

    drop table if exists permission cascade;

    drop table if exists role cascade;

    drop table if exists role_permissions cascade;

    drop table if exists season cascade;

    drop table if exists season_episodes cascade;

    drop table if exists serie cascade;

    drop table if exists users_watched_movies cascade;

    drop table if exists users_watched_series cascade;

    drop sequence if exists permission_sequence;

    drop sequence if exists role_sequence;

    drop sequence if exists user_sequence;
create sequence permission_sequence start 1 increment 1;
create sequence role_sequence start 1 increment 1;
create sequence user_sequence start 1 increment 1;

    create table _user (
       id int8 not null,
        background_picture varchar(255),
        country varchar(255),
        date_created timestamp,
        first_name varchar(255),
        is_account_non_expired boolean,
        is_account_non_locked boolean,
        is_account_private boolean,
        is_credentials_non_expired boolean,
        is_deleted boolean,
        is_enabled boolean,
        is_notifications_active boolean,
        is_notifications_comments_active boolean,
        is_notifications_trophies_active boolean,
        last_name varchar(255),
        _password varchar(255),
        profile_picture varchar(64),
        total_episodes_watched_number int8,
        total_movie_watched_number int8,
        total_movie_watched_time int8,
        total_series_watched_number int8,
        total_series_watched_time int8,
        email varchar(255),
        role_id int8,
        primary key (id)
    );

    create table episode (
       id int8 not null,
        imbd_id int8,
        name varchar(255),
        watched boolean,
        primary key (id)
    );

    create table movie (
       id int8 not null,
        name varchar(255),
        primary key (id)
    );

    create table permission (
       id int8 not null,
        description varchar(255),
        display_name varchar(255),
        permission varchar(255),
        primary key (id)
    );

    create table role (
       id int8 not null,
        description varchar(255),
        display_name varchar(255),
        role varchar(255),
        primary key (id)
    );

    create table role_permissions (
       role_id int8 not null,
        permission_id int8 not null,
        primary key (role_id, permission_id)
    );

    create table season (
       id int8 not null,
        name varchar(255),
        season_number varchar(255),
        primary key (id)
    );

    create table season_episodes (
       season_id int8 not null,
        episodes_id int8 not null
    );

    create table serie (
       id int8 not null,
        imdb_id int8,
        name varchar(255),
        primary key (id)
    );

    create table users_watched_movies (
       user_id int8 not null,
        movie_id int8 not null,
        primary key (user_id, movie_id)
    );

    create table users_watched_series (
       user_id int8 not null,
        serie_id int8 not null,
        primary key (user_id, serie_id)
    );

    alter table _user 
       add constraint UK_k11y3pdtsrjgy8w9b6q4bjwrx unique (email);

    alter table permission 
       add constraint UK_9kwkevw5na26e6qb4cbcbxaa4 unique (permission);

    alter table role 
       add constraint UK_bjxn5ii7v7ygwx39et0wawu0q unique (role);

    alter table season_episodes 
       add constraint UK_3n2w856u2hpxhgkuo5o46oiof unique (episodes_id);

    alter table _user 
       add constraint FK7wki6bxsew9feo0m49h59hiec 
       foreign key (role_id) 
       references role;

    alter table role_permissions 
       add constraint FKh0v7u4w7mttcu81o8wegayr8e 
       foreign key (permission_id) 
       references permission;

    alter table role_permissions 
       add constraint FKlodb7xh4a2xjv39gc3lsop95n 
       foreign key (role_id) 
       references role;

    alter table season_episodes 
       add constraint FKsxx2395f319y29g2l3kkmpg7t 
       foreign key (episodes_id) 
       references episode;

    alter table season_episodes 
       add constraint FKkye079mxw7jbjgfb4s01y1exa 
       foreign key (season_id) 
       references season;

    alter table users_watched_movies 
       add constraint FKlypo8hs1hkiaiqcrv4y2q5kgp 
       foreign key (movie_id) 
       references movie;

    alter table users_watched_movies 
       add constraint FKiul12g2u3b8m4xg6fje90wer8 
       foreign key (user_id) 
       references _user;

    alter table users_watched_series 
       add constraint FKpudtag2nf22r6x8eyegwn6rd0 
       foreign key (serie_id) 
       references serie;

    alter table users_watched_series 
       add constraint FK8dt3byf2l15ilneartbj5ilta 
       foreign key (user_id) 
       references _user;

    alter table _user 
       drop constraint FK7wki6bxsew9feo0m49h59hiec;

    alter table role_permissions 
       drop constraint FKh0v7u4w7mttcu81o8wegayr8e;

    alter table role_permissions 
       drop constraint FKlodb7xh4a2xjv39gc3lsop95n;

    alter table season_episodes 
       drop constraint FKsxx2395f319y29g2l3kkmpg7t;

    alter table season_episodes 
       drop constraint FKkye079mxw7jbjgfb4s01y1exa;

    alter table users_watched_movies 
       drop constraint FKlypo8hs1hkiaiqcrv4y2q5kgp;

    alter table users_watched_movies 
       drop constraint FKiul12g2u3b8m4xg6fje90wer8;

    alter table users_watched_series 
       drop constraint FKpudtag2nf22r6x8eyegwn6rd0;

    alter table users_watched_series 
       drop constraint FK8dt3byf2l15ilneartbj5ilta;

    drop table if exists _user cascade;

    drop table if exists episode cascade;

    drop table if exists movie cascade;

    drop table if exists permission cascade;

    drop table if exists role cascade;

    drop table if exists role_permissions cascade;

    drop table if exists season cascade;

    drop table if exists season_episodes cascade;

    drop table if exists serie cascade;

    drop table if exists users_watched_movies cascade;

    drop table if exists users_watched_series cascade;

    drop sequence if exists permission_sequence;

    drop sequence if exists role_sequence;

    drop sequence if exists user_sequence;
create sequence permission_sequence start 1 increment 1;
create sequence role_sequence start 1 increment 1;
create sequence user_sequence start 1 increment 1;

    create table _user (
       id int8 not null,
        background_picture varchar(255),
        country varchar(255),
        date_created timestamp,
        first_name varchar(255),
        is_account_non_expired boolean,
        is_account_non_locked boolean,
        is_account_private boolean,
        is_credentials_non_expired boolean,
        is_deleted boolean,
        is_enabled boolean,
        is_notifications_active boolean,
        is_notifications_comments_active boolean,
        is_notifications_trophies_active boolean,
        last_name varchar(255),
        _password varchar(255),
        profile_picture varchar(64),
        total_episodes_watched_number int8,
        total_movie_watched_number int8,
        total_movie_watched_time int8,
        total_series_watched_number int8,
        total_series_watched_time int8,
        email varchar(255),
        role_id int8,
        primary key (id)
    );

    create table episode (
       id int8 not null,
        imbd_id int8,
        name varchar(255),
        watched boolean,
        primary key (id)
    );

    create table movie (
       id int8 not null,
        name varchar(255),
        primary key (id)
    );

    create table permission (
       id int8 not null,
        description varchar(255),
        display_name varchar(255),
        permission varchar(255),
        primary key (id)
    );

    create table role (
       id int8 not null,
        description varchar(255),
        description2 varchar(255),
        display_name varchar(255),
        role varchar(255),
        primary key (id)
    );

    create table role_permissions (
       role_id int8 not null,
        permission_id int8 not null,
        primary key (role_id, permission_id)
    );

    create table season (
       id int8 not null,
        name varchar(255),
        season_number varchar(255),
        primary key (id)
    );

    create table season_episodes (
       season_id int8 not null,
        episodes_id int8 not null
    );

    create table serie (
       id int8 not null,
        imdb_id int8,
        name varchar(255),
        primary key (id)
    );

    create table users_watched_movies (
       user_id int8 not null,
        movie_id int8 not null,
        primary key (user_id, movie_id)
    );

    create table users_watched_series (
       user_id int8 not null,
        serie_id int8 not null,
        primary key (user_id, serie_id)
    );

    alter table _user 
       add constraint UK_k11y3pdtsrjgy8w9b6q4bjwrx unique (email);

    alter table permission 
       add constraint UK_9kwkevw5na26e6qb4cbcbxaa4 unique (permission);

    alter table role 
       add constraint UK_bjxn5ii7v7ygwx39et0wawu0q unique (role);

    alter table season_episodes 
       add constraint UK_3n2w856u2hpxhgkuo5o46oiof unique (episodes_id);

    alter table _user 
       add constraint FK7wki6bxsew9feo0m49h59hiec 
       foreign key (role_id) 
       references role;

    alter table role_permissions 
       add constraint FKh0v7u4w7mttcu81o8wegayr8e 
       foreign key (permission_id) 
       references permission;

    alter table role_permissions 
       add constraint FKlodb7xh4a2xjv39gc3lsop95n 
       foreign key (role_id) 
       references role;

    alter table season_episodes 
       add constraint FKsxx2395f319y29g2l3kkmpg7t 
       foreign key (episodes_id) 
       references episode;

    alter table season_episodes 
       add constraint FKkye079mxw7jbjgfb4s01y1exa 
       foreign key (season_id) 
       references season;

    alter table users_watched_movies 
       add constraint FKlypo8hs1hkiaiqcrv4y2q5kgp 
       foreign key (movie_id) 
       references movie;

    alter table users_watched_movies 
       add constraint FKiul12g2u3b8m4xg6fje90wer8 
       foreign key (user_id) 
       references _user;

    alter table users_watched_series 
       add constraint FKpudtag2nf22r6x8eyegwn6rd0 
       foreign key (serie_id) 
       references serie;

    alter table users_watched_series 
       add constraint FK8dt3byf2l15ilneartbj5ilta 
       foreign key (user_id) 
       references _user;

    alter table _user 
       drop constraint FK7wki6bxsew9feo0m49h59hiec;

    alter table role_permissions 
       drop constraint FKh0v7u4w7mttcu81o8wegayr8e;

    alter table role_permissions 
       drop constraint FKlodb7xh4a2xjv39gc3lsop95n;

    alter table season_episodes 
       drop constraint FKsxx2395f319y29g2l3kkmpg7t;

    alter table season_episodes 
       drop constraint FKkye079mxw7jbjgfb4s01y1exa;

    alter table users_watched_movies 
       drop constraint FKlypo8hs1hkiaiqcrv4y2q5kgp;

    alter table users_watched_movies 
       drop constraint FKiul12g2u3b8m4xg6fje90wer8;

    alter table users_watched_series 
       drop constraint FKpudtag2nf22r6x8eyegwn6rd0;

    alter table users_watched_series 
       drop constraint FK8dt3byf2l15ilneartbj5ilta;

    drop table if exists _user cascade;

    drop table if exists episode cascade;

    drop table if exists movie cascade;

    drop table if exists permission cascade;

    drop table if exists role cascade;

    drop table if exists role_permissions cascade;

    drop table if exists season cascade;

    drop table if exists season_episodes cascade;

    drop table if exists serie cascade;

    drop table if exists users_watched_movies cascade;

    drop table if exists users_watched_series cascade;

    drop sequence if exists permission_sequence;

    drop sequence if exists role_sequence;

    drop sequence if exists user_sequence;
create sequence permission_sequence start 1 increment 1;
create sequence role_sequence start 1 increment 1;
create sequence user_sequence start 1 increment 1;

    create table _user (
       id int8 not null,
        background_picture varchar(255),
        country varchar(255),
        date_created timestamp,
        first_name varchar(255),
        is_account_non_expired boolean,
        is_account_non_locked boolean,
        is_account_private boolean,
        is_credentials_non_expired boolean,
        is_deleted boolean,
        is_enabled boolean,
        is_notifications_active boolean,
        is_notifications_comments_active boolean,
        is_notifications_trophies_active boolean,
        last_name varchar(255),
        _password varchar(255),
        profile_picture varchar(64),
        total_episodes_watched_number int8,
        total_movie_watched_number int8,
        total_movie_watched_time int8,
        total_series_watched_number int8,
        total_series_watched_time int8,
        email varchar(255),
        role_id int8,
        primary key (id)
    );

    create table episode (
       id int8 not null,
        imbd_id int8,
        name varchar(255),
        watched boolean,
        primary key (id)
    );

    create table movie (
       id int8 not null,
        name varchar(255),
        primary key (id)
    );

    create table permission (
       id int8 not null,
        description varchar(255),
        display_name varchar(255),
        permission varchar(255),
        primary key (id)
    );

    create table role (
       id int8 not null,
        description varchar(255),
        description2 varchar(255),
        display_name varchar(255),
        role varchar(255),
        primary key (id)
    );

    create table role_permissions (
       role_id int8 not null,
        permission_id int8 not null,
        primary key (role_id, permission_id)
    );

    create table season (
       id int8 not null,
        name varchar(255),
        season_number varchar(255),
        primary key (id)
    );

    create table season_episodes (
       season_id int8 not null,
        episodes_id int8 not null
    );

    create table serie (
       id int8 not null,
        imdb_id int8,
        name varchar(255),
        primary key (id)
    );

    create table users_watched_movies (
       user_id int8 not null,
        movie_id int8 not null,
        primary key (user_id, movie_id)
    );

    create table users_watched_series (
       user_id int8 not null,
        serie_id int8 not null,
        primary key (user_id, serie_id)
    );

    alter table _user 
       add constraint UK_k11y3pdtsrjgy8w9b6q4bjwrx unique (email);

    alter table permission 
       add constraint UK_9kwkevw5na26e6qb4cbcbxaa4 unique (permission);

    alter table role 
       add constraint UK_bjxn5ii7v7ygwx39et0wawu0q unique (role);

    alter table season_episodes 
       add constraint UK_3n2w856u2hpxhgkuo5o46oiof unique (episodes_id);

    alter table _user 
       add constraint FK7wki6bxsew9feo0m49h59hiec 
       foreign key (role_id) 
       references role;

    alter table role_permissions 
       add constraint FKh0v7u4w7mttcu81o8wegayr8e 
       foreign key (permission_id) 
       references permission;

    alter table role_permissions 
       add constraint FKlodb7xh4a2xjv39gc3lsop95n 
       foreign key (role_id) 
       references role;

    alter table season_episodes 
       add constraint FKsxx2395f319y29g2l3kkmpg7t 
       foreign key (episodes_id) 
       references episode;

    alter table season_episodes 
       add constraint FKkye079mxw7jbjgfb4s01y1exa 
       foreign key (season_id) 
       references season;

    alter table users_watched_movies 
       add constraint FKlypo8hs1hkiaiqcrv4y2q5kgp 
       foreign key (movie_id) 
       references movie;

    alter table users_watched_movies 
       add constraint FKiul12g2u3b8m4xg6fje90wer8 
       foreign key (user_id) 
       references _user;

    alter table users_watched_series 
       add constraint FKpudtag2nf22r6x8eyegwn6rd0 
       foreign key (serie_id) 
       references serie;

    alter table users_watched_series 
       add constraint FK8dt3byf2l15ilneartbj5ilta 
       foreign key (user_id) 
       references _user;

    alter table _user 
       drop constraint FK7wki6bxsew9feo0m49h59hiec;

    alter table role_permissions 
       drop constraint FKh0v7u4w7mttcu81o8wegayr8e;

    alter table role_permissions 
       drop constraint FKlodb7xh4a2xjv39gc3lsop95n;

    alter table season_episodes 
       drop constraint FKsxx2395f319y29g2l3kkmpg7t;

    alter table season_episodes 
       drop constraint FKkye079mxw7jbjgfb4s01y1exa;

    alter table users_watched_movies 
       drop constraint FKlypo8hs1hkiaiqcrv4y2q5kgp;

    alter table users_watched_movies 
       drop constraint FKiul12g2u3b8m4xg6fje90wer8;

    alter table users_watched_series 
       drop constraint FKpudtag2nf22r6x8eyegwn6rd0;

    alter table users_watched_series 
       drop constraint FK8dt3byf2l15ilneartbj5ilta;

    drop table if exists _user cascade;

    drop table if exists episode cascade;

    drop table if exists movie cascade;

    drop table if exists permission cascade;

    drop table if exists role cascade;

    drop table if exists role_permissions cascade;

    drop table if exists season cascade;

    drop table if exists season_episodes cascade;

    drop table if exists serie cascade;

    drop table if exists users_watched_movies cascade;

    drop table if exists users_watched_series cascade;

    drop sequence if exists permission_sequence;

    drop sequence if exists role_sequence;

    drop sequence if exists user_sequence;
create sequence permission_sequence start 1 increment 1;
create sequence role_sequence start 1 increment 1;
create sequence user_sequence start 1 increment 1;

    create table _user (
       id int8 not null,
        background_picture varchar(255),
        country varchar(255),
        date_created timestamp,
        first_name varchar(255),
        is_account_non_expired boolean,
        is_account_non_locked boolean,
        is_account_private boolean,
        is_credentials_non_expired boolean,
        is_deleted boolean,
        is_enabled boolean,
        is_notifications_active boolean,
        is_notifications_comments_active boolean,
        is_notifications_trophies_active boolean,
        last_name varchar(255),
        _password varchar(255),
        profile_picture varchar(64),
        total_episodes_watched_number int8,
        total_movie_watched_number int8,
        total_movie_watched_time int8,
        total_series_watched_number int8,
        total_series_watched_time int8,
        email varchar(255),
        role_id int8,
        primary key (id)
    );

    create table episode (
       id int8 not null,
        imbd_id int8,
        name varchar(255),
        watched boolean,
        primary key (id)
    );

    create table movie (
       id int8 not null,
        name varchar(255),
        primary key (id)
    );

    create table permission (
       id int8 not null,
        description varchar(255),
        display_name varchar(255),
        permission varchar(255),
        primary key (id)
    );

    create table role (
       id int8 not null,
        description varchar(255),
        description2 varchar(255),
        display_name varchar(255),
        role varchar(255),
        primary key (id)
    );

    create table role_permissions (
       role_id int8 not null,
        permission_id int8 not null,
        primary key (role_id, permission_id)
    );

    create table season (
       id int8 not null,
        name varchar(255),
        season_number varchar(255),
        primary key (id)
    );

    create table season_episodes (
       season_id int8 not null,
        episodes_id int8 not null
    );

    create table serie (
       id int8 not null,
        imdb_id int8,
        name varchar(255),
        primary key (id)
    );

    create table users_watched_movies (
       user_id int8 not null,
        movie_id int8 not null,
        primary key (user_id, movie_id)
    );

    create table users_watched_series (
       user_id int8 not null,
        serie_id int8 not null,
        primary key (user_id, serie_id)
    );

    alter table _user 
       add constraint UK_k11y3pdtsrjgy8w9b6q4bjwrx unique (email);

    alter table permission 
       add constraint UK_9kwkevw5na26e6qb4cbcbxaa4 unique (permission);

    alter table role 
       add constraint UK_bjxn5ii7v7ygwx39et0wawu0q unique (role);

    alter table season_episodes 
       add constraint UK_3n2w856u2hpxhgkuo5o46oiof unique (episodes_id);

    alter table _user 
       add constraint FK7wki6bxsew9feo0m49h59hiec 
       foreign key (role_id) 
       references role;

    alter table role_permissions 
       add constraint FKh0v7u4w7mttcu81o8wegayr8e 
       foreign key (permission_id) 
       references permission;

    alter table role_permissions 
       add constraint FKlodb7xh4a2xjv39gc3lsop95n 
       foreign key (role_id) 
       references role;

    alter table season_episodes 
       add constraint FKsxx2395f319y29g2l3kkmpg7t 
       foreign key (episodes_id) 
       references episode;

    alter table season_episodes 
       add constraint FKkye079mxw7jbjgfb4s01y1exa 
       foreign key (season_id) 
       references season;

    alter table users_watched_movies 
       add constraint FKlypo8hs1hkiaiqcrv4y2q5kgp 
       foreign key (movie_id) 
       references movie;

    alter table users_watched_movies 
       add constraint FKiul12g2u3b8m4xg6fje90wer8 
       foreign key (user_id) 
       references _user;

    alter table users_watched_series 
       add constraint FKpudtag2nf22r6x8eyegwn6rd0 
       foreign key (serie_id) 
       references serie;

    alter table users_watched_series 
       add constraint FK8dt3byf2l15ilneartbj5ilta 
       foreign key (user_id) 
       references _user;

    alter table _user 
       drop constraint FK7wki6bxsew9feo0m49h59hiec;

    alter table role_permissions 
       drop constraint FKh0v7u4w7mttcu81o8wegayr8e;

    alter table role_permissions 
       drop constraint FKlodb7xh4a2xjv39gc3lsop95n;

    alter table season_episodes 
       drop constraint FKsxx2395f319y29g2l3kkmpg7t;

    alter table season_episodes 
       drop constraint FKkye079mxw7jbjgfb4s01y1exa;

    alter table users_watched_movies 
       drop constraint FKlypo8hs1hkiaiqcrv4y2q5kgp;

    alter table users_watched_movies 
       drop constraint FKiul12g2u3b8m4xg6fje90wer8;

    alter table users_watched_series 
       drop constraint FKpudtag2nf22r6x8eyegwn6rd0;

    alter table users_watched_series 
       drop constraint FK8dt3byf2l15ilneartbj5ilta;

    drop table if exists _user cascade;

    drop table if exists episode cascade;

    drop table if exists movie cascade;

    drop table if exists permission cascade;

    drop table if exists role cascade;

    drop table if exists role_permissions cascade;

    drop table if exists season cascade;

    drop table if exists season_episodes cascade;

    drop table if exists serie cascade;

    drop table if exists users_watched_movies cascade;

    drop table if exists users_watched_series cascade;

    drop sequence if exists permission_sequence;

    drop sequence if exists role_sequence;

    drop sequence if exists user_sequence;
create sequence permission_sequence start 1 increment 1;
create sequence role_sequence start 1 increment 1;
create sequence user_sequence start 1 increment 1;

    create table _user (
       id int8 not null,
        background_picture varchar(255),
        country varchar(255),
        date_created timestamp,
        first_name varchar(255),
        is_account_non_expired boolean,
        is_account_non_locked boolean,
        is_account_private boolean,
        is_credentials_non_expired boolean,
        is_deleted boolean,
        is_enabled boolean,
        is_notifications_active boolean,
        is_notifications_comments_active boolean,
        is_notifications_trophies_active boolean,
        last_name varchar(255),
        _password varchar(255),
        profile_picture varchar(64),
        total_episodes_watched_number int8,
        total_movie_watched_number int8,
        total_movie_watched_time int8,
        total_series_watched_number int8,
        total_series_watched_time int8,
        email varchar(255),
        role_id int8,
        primary key (id)
    );

    create table episode (
       id int8 not null,
        imbd_id int8,
        name varchar(255),
        watched boolean,
        primary key (id)
    );

    create table movie (
       id int8 not null,
        name varchar(255),
        primary key (id)
    );

    create table permission (
       id int8 not null,
        description varchar(255),
        display_name varchar(255),
        permission varchar(255),
        primary key (id)
    );

    create table role (
       id int8 not null,
        description varchar(255),
        description2 varchar(255),
        display_name varchar(255),
        role varchar(255),
        primary key (id)
    );

    create table role_permissions (
       role_id int8 not null,
        permission_id int8 not null,
        primary key (role_id, permission_id)
    );

    create table season (
       id int8 not null,
        name varchar(255),
        season_number varchar(255),
        primary key (id)
    );

    create table season_episodes (
       season_id int8 not null,
        episodes_id int8 not null
    );

    create table serie (
       id int8 not null,
        imdb_id int8,
        name varchar(255),
        primary key (id)
    );

    create table users_watched_movies (
       user_id int8 not null,
        movie_id int8 not null,
        primary key (user_id, movie_id)
    );

    create table users_watched_series (
       user_id int8 not null,
        serie_id int8 not null,
        primary key (user_id, serie_id)
    );

    alter table _user 
       add constraint UK_k11y3pdtsrjgy8w9b6q4bjwrx unique (email);

    alter table permission 
       add constraint UK_9kwkevw5na26e6qb4cbcbxaa4 unique (permission);

    alter table role 
       add constraint UK_bjxn5ii7v7ygwx39et0wawu0q unique (role);

    alter table season_episodes 
       add constraint UK_3n2w856u2hpxhgkuo5o46oiof unique (episodes_id);

    alter table _user 
       add constraint FK7wki6bxsew9feo0m49h59hiec 
       foreign key (role_id) 
       references role;

    alter table role_permissions 
       add constraint FKh0v7u4w7mttcu81o8wegayr8e 
       foreign key (permission_id) 
       references permission;

    alter table role_permissions 
       add constraint FKlodb7xh4a2xjv39gc3lsop95n 
       foreign key (role_id) 
       references role;

    alter table season_episodes 
       add constraint FKsxx2395f319y29g2l3kkmpg7t 
       foreign key (episodes_id) 
       references episode;

    alter table season_episodes 
       add constraint FKkye079mxw7jbjgfb4s01y1exa 
       foreign key (season_id) 
       references season;

    alter table users_watched_movies 
       add constraint FKlypo8hs1hkiaiqcrv4y2q5kgp 
       foreign key (movie_id) 
       references movie;

    alter table users_watched_movies 
       add constraint FKiul12g2u3b8m4xg6fje90wer8 
       foreign key (user_id) 
       references _user;

    alter table users_watched_series 
       add constraint FKpudtag2nf22r6x8eyegwn6rd0 
       foreign key (serie_id) 
       references serie;

    alter table users_watched_series 
       add constraint FK8dt3byf2l15ilneartbj5ilta 
       foreign key (user_id) 
       references _user;

    alter table _user 
       drop constraint FK7wki6bxsew9feo0m49h59hiec;

    alter table role_permissions 
       drop constraint FKh0v7u4w7mttcu81o8wegayr8e;

    alter table role_permissions 
       drop constraint FKlodb7xh4a2xjv39gc3lsop95n;

    alter table season_episodes 
       drop constraint FKsxx2395f319y29g2l3kkmpg7t;

    alter table season_episodes 
       drop constraint FKkye079mxw7jbjgfb4s01y1exa;

    alter table users_watched_movies 
       drop constraint FKlypo8hs1hkiaiqcrv4y2q5kgp;

    alter table users_watched_movies 
       drop constraint FKiul12g2u3b8m4xg6fje90wer8;

    alter table users_watched_series 
       drop constraint FKpudtag2nf22r6x8eyegwn6rd0;

    alter table users_watched_series 
       drop constraint FK8dt3byf2l15ilneartbj5ilta;

    drop table if exists _user cascade;

    drop table if exists episode cascade;

    drop table if exists movie cascade;

    drop table if exists permission cascade;

    drop table if exists role cascade;

    drop table if exists role_permissions cascade;

    drop table if exists season cascade;

    drop table if exists season_episodes cascade;

    drop table if exists serie cascade;

    drop table if exists users_watched_movies cascade;

    drop table if exists users_watched_series cascade;

    drop sequence if exists permission_sequence;

    drop sequence if exists role_sequence;

    drop sequence if exists user_sequence;
create sequence permission_sequence start 1 increment 1;
create sequence role_sequence start 1 increment 1;
create sequence user_sequence start 1 increment 1;

    create table _user (
       id int8 not null,
        background_picture varchar(255),
        country varchar(255),
        date_created timestamp,
        first_name varchar(255),
        is_account_non_expired boolean,
        is_account_non_locked boolean,
        is_account_private boolean,
        is_credentials_non_expired boolean,
        is_deleted boolean,
        is_enabled boolean,
        is_notifications_active boolean,
        is_notifications_comments_active boolean,
        is_notifications_trophies_active boolean,
        last_name varchar(255),
        _password varchar(255),
        profile_picture varchar(64),
        total_episodes_watched_number int8,
        total_movie_watched_number int8,
        total_movie_watched_time int8,
        total_series_watched_number int8,
        total_series_watched_time int8,
        email varchar(255),
        role_id int8,
        primary key (id)
    );

    create table episode (
       id int8 not null,
        imbd_id int8,
        name varchar(255),
        watched boolean,
        primary key (id)
    );

    create table movie (
       id int8 not null,
        name varchar(255),
        primary key (id)
    );

    create table permission (
       id int8 not null,
        description varchar(255),
        display_name varchar(255),
        permission varchar(255),
        primary key (id)
    );

    create table role (
       id int8 not null,
        description varchar(255),
        description2 varchar(255),
        display_name varchar(255),
        role varchar(255),
        primary key (id)
    );

    create table role_permissions (
       role_id int8 not null,
        permission_id int8 not null,
        primary key (role_id, permission_id)
    );

    create table season (
       id int8 not null,
        name varchar(255),
        season_number varchar(255),
        primary key (id)
    );

    create table season_episodes (
       season_id int8 not null,
        episodes_id int8 not null
    );

    create table serie (
       id int8 not null,
        imdb_id int8,
        name varchar(255),
        primary key (id)
    );

    create table users_watched_movies (
       user_id int8 not null,
        movie_id int8 not null,
        primary key (user_id, movie_id)
    );

    create table users_watched_series (
       user_id int8 not null,
        serie_id int8 not null,
        primary key (user_id, serie_id)
    );

    alter table _user 
       add constraint UK_k11y3pdtsrjgy8w9b6q4bjwrx unique (email);

    alter table permission 
       add constraint UK_9kwkevw5na26e6qb4cbcbxaa4 unique (permission);

    alter table role 
       add constraint UK_bjxn5ii7v7ygwx39et0wawu0q unique (role);

    alter table season_episodes 
       add constraint UK_3n2w856u2hpxhgkuo5o46oiof unique (episodes_id);

    alter table _user 
       add constraint FK7wki6bxsew9feo0m49h59hiec 
       foreign key (role_id) 
       references role;

    alter table role_permissions 
       add constraint FKh0v7u4w7mttcu81o8wegayr8e 
       foreign key (permission_id) 
       references permission;

    alter table role_permissions 
       add constraint FKlodb7xh4a2xjv39gc3lsop95n 
       foreign key (role_id) 
       references role;

    alter table season_episodes 
       add constraint FKsxx2395f319y29g2l3kkmpg7t 
       foreign key (episodes_id) 
       references episode;

    alter table season_episodes 
       add constraint FKkye079mxw7jbjgfb4s01y1exa 
       foreign key (season_id) 
       references season;

    alter table users_watched_movies 
       add constraint FKlypo8hs1hkiaiqcrv4y2q5kgp 
       foreign key (movie_id) 
       references movie;

    alter table users_watched_movies 
       add constraint FKiul12g2u3b8m4xg6fje90wer8 
       foreign key (user_id) 
       references _user;

    alter table users_watched_series 
       add constraint FKpudtag2nf22r6x8eyegwn6rd0 
       foreign key (serie_id) 
       references serie;

    alter table users_watched_series 
       add constraint FK8dt3byf2l15ilneartbj5ilta 
       foreign key (user_id) 
       references _user;

    alter table _user 
       drop constraint FK7wki6bxsew9feo0m49h59hiec;

    alter table role_permissions 
       drop constraint FKh0v7u4w7mttcu81o8wegayr8e;

    alter table role_permissions 
       drop constraint FKlodb7xh4a2xjv39gc3lsop95n;

    alter table season_episodes 
       drop constraint FKsxx2395f319y29g2l3kkmpg7t;

    alter table season_episodes 
       drop constraint FKkye079mxw7jbjgfb4s01y1exa;

    alter table users_watched_movies 
       drop constraint FKlypo8hs1hkiaiqcrv4y2q5kgp;

    alter table users_watched_movies 
       drop constraint FKiul12g2u3b8m4xg6fje90wer8;

    alter table users_watched_series 
       drop constraint FKpudtag2nf22r6x8eyegwn6rd0;

    alter table users_watched_series 
       drop constraint FK8dt3byf2l15ilneartbj5ilta;

    drop table if exists _user cascade;

    drop table if exists episode cascade;

    drop table if exists movie cascade;

    drop table if exists permission cascade;

    drop table if exists role cascade;

    drop table if exists role_permissions cascade;

    drop table if exists season cascade;

    drop table if exists season_episodes cascade;

    drop table if exists serie cascade;

    drop table if exists users_watched_movies cascade;

    drop table if exists users_watched_series cascade;

    drop sequence if exists permission_sequence;

    drop sequence if exists role_sequence;

    drop sequence if exists user_sequence;
create sequence permission_sequence start 1 increment 1;
create sequence role_sequence start 1 increment 1;
create sequence user_sequence start 1 increment 1;

    create table _user (
       id int8 not null,
        background_picture varchar(255),
        country varchar(255),
        date_created timestamp,
        first_name varchar(255),
        is_account_non_expired boolean,
        is_account_non_locked boolean,
        is_account_private boolean,
        is_credentials_non_expired boolean,
        is_deleted boolean,
        is_enabled boolean,
        is_notifications_active boolean,
        is_notifications_comments_active boolean,
        is_notifications_trophies_active boolean,
        last_name varchar(255),
        _password varchar(255),
        profile_picture varchar(64),
        total_episodes_watched_number int8,
        total_movie_watched_number int8,
        total_movie_watched_time int8,
        total_series_watched_number int8,
        total_series_watched_time int8,
        email varchar(255),
        role_id int8,
        primary key (id)
    );

    create table episode (
       id int8 not null,
        imbd_id int8,
        name varchar(255),
        watched boolean,
        primary key (id)
    );

    create table movie (
       id int8 not null,
        name varchar(255),
        primary key (id)
    );

    create table permission (
       id int8 not null,
        description varchar(255),
        display_name varchar(255),
        permission varchar(255),
        primary key (id)
    );

    create table role (
       id int8 not null,
        description varchar(255),
        display_name varchar(255),
        role varchar(255),
        primary key (id)
    );

    create table role_permissions (
       role_id int8 not null,
        permission_id int8 not null,
        primary key (role_id, permission_id)
    );

    create table season (
       id int8 not null,
        name varchar(255),
        season_number varchar(255),
        primary key (id)
    );

    create table season_episodes (
       season_id int8 not null,
        episodes_id int8 not null
    );

    create table serie (
       id int8 not null,
        imdb_id int8,
        name varchar(255),
        primary key (id)
    );

    create table users_watched_movies (
       user_id int8 not null,
        movie_id int8 not null,
        primary key (user_id, movie_id)
    );

    create table users_watched_series (
       user_id int8 not null,
        serie_id int8 not null,
        primary key (user_id, serie_id)
    );

    alter table _user 
       add constraint UK_k11y3pdtsrjgy8w9b6q4bjwrx unique (email);

    alter table permission 
       add constraint UK_9kwkevw5na26e6qb4cbcbxaa4 unique (permission);

    alter table role 
       add constraint UK_bjxn5ii7v7ygwx39et0wawu0q unique (role);

    alter table season_episodes 
       add constraint UK_3n2w856u2hpxhgkuo5o46oiof unique (episodes_id);

    alter table _user 
       add constraint FK7wki6bxsew9feo0m49h59hiec 
       foreign key (role_id) 
       references role;

    alter table role_permissions 
       add constraint FKh0v7u4w7mttcu81o8wegayr8e 
       foreign key (permission_id) 
       references permission;

    alter table role_permissions 
       add constraint FKlodb7xh4a2xjv39gc3lsop95n 
       foreign key (role_id) 
       references role;

    alter table season_episodes 
       add constraint FKsxx2395f319y29g2l3kkmpg7t 
       foreign key (episodes_id) 
       references episode;

    alter table season_episodes 
       add constraint FKkye079mxw7jbjgfb4s01y1exa 
       foreign key (season_id) 
       references season;

    alter table users_watched_movies 
       add constraint FKlypo8hs1hkiaiqcrv4y2q5kgp 
       foreign key (movie_id) 
       references movie;

    alter table users_watched_movies 
       add constraint FKiul12g2u3b8m4xg6fje90wer8 
       foreign key (user_id) 
       references _user;

    alter table users_watched_series 
       add constraint FKpudtag2nf22r6x8eyegwn6rd0 
       foreign key (serie_id) 
       references serie;

    alter table users_watched_series 
       add constraint FK8dt3byf2l15ilneartbj5ilta 
       foreign key (user_id) 
       references _user;
