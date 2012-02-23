
    alter table acct_group_permission 
        drop foreign key FKAE243466DE3FB930;

    alter table acct_user_group 
        drop foreign key FKFE85CB3EDE3FB930;

    alter table acct_user_group 
        drop foreign key FKFE85CB3E836A7D10;

    drop table if exists acct_group;

    drop table if exists acct_group_permission;

    drop table if exists acct_user;

    drop table if exists acct_user_group;

    create table acct_group (
        id bigint not null auto_increment,
        name varchar(255) not null unique,
        primary key (id)
    ) ENGINE=InnoDB;

    create table acct_group_permission (
        group_id bigint not null,
        permission varchar(255) not null
    ) ENGINE=InnoDB;

    create table acct_user (
        id bigint not null auto_increment,
        email varchar(255),
        login_name varchar(255) not null unique,
        name varchar(255),
        password varchar(255),
        primary key (id)
    ) ENGINE=InnoDB;

    create table acct_user_group (
        user_id bigint not null,
        group_id bigint not null
    ) ENGINE=InnoDB;

    alter table acct_group_permission 
        add constraint FKAE243466DE3FB930 
        foreign key (group_id) 
        references acct_group (id);

    alter table acct_user_group 
        add constraint FKFE85CB3EDE3FB930 
        foreign key (group_id) 
        references acct_group (id);

    alter table acct_user_group 
        add constraint FKFE85CB3E836A7D10 
        foreign key (user_id) 
        references acct_user (id);
