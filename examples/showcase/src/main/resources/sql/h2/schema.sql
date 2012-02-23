    drop table if exists ss_user_role;

    drop table if exists ss_role;

    drop table if exists ss_user;
	
	drop table if exists ss_post;
    
    drop table if exists ss_log;


    create table ss_post (
        id varchar(16),
    	dtype varchar(32) not null,
        content clob,
        modify_time timestamp,
        title varchar(255) not null,
        user_id varchar(16),
        subject_id varchar(16),
        primary key (id)
    );

    create table ss_role (
        id varchar(16),
    	name varchar(255) not null unique,
        primary key (id)
    );

    create table ss_user (
        id varchar(16),
    	create_by varchar(255),
        create_time timestamp,
        last_modify_by varchar(255),
        last_modify_time timestamp,
        email varchar(255),
        login_name varchar(255) not null unique,
        name varchar(255),
        plain_password varchar(255),
        sha_password varchar(255),
        status varchar(255),
        version integer,
        primary key (id)
    );

    create table ss_user_role (
        user_id varchar(16) not null,
        role_id varchar(16) not null,
        primary key (user_id, role_id)
    );
    
    create table ss_log (
    	thread_name varchar(255),
    	logger_name varchar(255),
    	log_time timestamp,
    	level varchar(20),
    	message varchar(255)
    );

    alter table ss_post 
        add constraint FK8D193F1F7334D076 
        foreign key (subject_id) 
        references ss_post;

    alter table ss_post 
        add constraint FK8D193F1FF125651E 
        foreign key (user_id) 
        references ss_user;

    alter table ss_user_role 
        add constraint FK1306854BF125651E 
        foreign key (user_id) 
        references ss_user;

    alter table ss_user_role 
        add constraint FK1306854B4BFAA13E 
        foreign key (role_id) 
        references ss_role;
