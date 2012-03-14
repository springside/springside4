    drop table if exists ss_user_role;

    drop table if exists ss_role;

    drop table if exists ss_user;

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
        primary key (id)
    );

    create table ss_user_role (
        user_id varchar(16) not null,
        role_id varchar(16) not null,
        primary key (user_id, role_id)
    );
    

    alter table ss_user_role 
        add constraint FK1306854BF125651E 
        foreign key (user_id) 
        references ss_user;

    alter table ss_user_role 
        add constraint FK1306854B4BFAA13E 
        foreign key (role_id) 
        references ss_role;
