#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
drop table ${tablePrefix}task;
drop table ${tablePrefix}user;

create table ${tablePrefix}task (
	id number(19,0),
	title varchar2(128) not null,
	description varchar2(255),
	user_id number(19,0) not null,
	primary key (id)
);

create table ${tablePrefix}user (
	id number(19,0),
	login_name varchar2(64) not null unique,
	name varchar2(64) not null,
	password varchar2(255) not null,
	salt varchar2(64) not null,
	roles varchar2(255) not null,
	register_date date not null,
	primary key (id)
);


create sequence ${tablePrefix}seq_task start with 100 increment by 20;
create sequence ${tablePrefix}seq_user start with 100 increment by 20;
