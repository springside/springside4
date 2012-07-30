    drop table if exists ss_user_role;

    drop table if exists ss_role;

    drop table if exists ss_user;
    
    drop table if exists ss_team;

    create table ss_role (
        id varchar(16),
    	name varchar(255) not null unique,
        primary key (id)
    );

    create table ss_user (
        id varchar(16),
        login_name varchar(255) not null unique,
        name varchar(64),
        password varchar(255),
        salt varchar(64),
        email varchar(128),
        status varchar(32),
        team_id varchar(16),
        primary key (id)
    );

    create table ss_user_role (
        user_id varchar(16) not null,
        role_id varchar(16) not null,
        primary key (user_id, role_id)
    );
    
   	create table ss_team (
        id varchar(16),
    	name varchar(255) not null unique,
    	master varchar(16),
        primary key (id)
    );

  
