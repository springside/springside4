
    drop table acct_group cascade constraints;

    drop table acct_group_permission cascade constraints;

    drop table acct_user cascade constraints;

    drop table acct_user_group cascade constraints;

    drop sequence hibernate_sequence;

    create table acct_group (
        id number(19,0) not null,
        name varchar2(255 char) not null unique,
        primary key (id)
    );

    create table acct_group_permission (
        group_id number(19,0) not null,
        permission varchar2(255 char) not null
    );

    create table acct_user (
        id number(19,0) not null,
        email varchar2(255 char),
        login_name varchar2(255 char) not null unique,
        name varchar2(255 char),
        password varchar2(255 char),
        primary key (id)
    );

    create table acct_user_group (
        user_id number(19,0) not null,
        group_id number(19,0) not null
    );

    alter table acct_group_permission 
        add constraint FKAE243466DE3FB930 
        foreign key (group_id) 
        references acct_group;

    alter table acct_user_group 
        add constraint FKFE85CB3EDE3FB930 
        foreign key (group_id) 
        references acct_group;

    alter table acct_user_group 
        add constraint FKFE85CB3E836A7D10 
        foreign key (user_id) 
        references acct_user;

    create sequence hibernate_sequence;
