drop table if exists ss_task;
drop table if exists ss_user;

create table ss_task (
	id bigint auto_increment,
	title varchar(128) not null,
	description varchar(255),
	user_id bigint,
    primary key (id)
) engine=InnoDB;

create table ss_user (
	id bigint auto_increment,
	login_name varchar(64) not null unique,
	name varchar(64),
	password varchar(255),
	salt varchar(64),
	roles varchar(255),
	primary key (id)
) engine=InnoDB;