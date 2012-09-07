drop table if exists ss_task;
drop table if exists ss_user;

create table ss_task (
	id bigint,
	title varchar(128) not null,
	description varchar(255),
	user_id bigint,
    primary key (id)
);

create table ss_user (
	id bigint,
	login_name varchar(64) not null unique,
	name varchar(64),
	password varchar(255),
	salt varchar(64),
	roles varchar(255),
	primary key (id)
);


create sequence ss_seq_task start with 100 increment by 20;
create sequence ss_seq_user start with 100 increment by 20;