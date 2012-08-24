insert into SS_USER (ID,LOGIN_NAME,NAME,EMAIL,PASSWORD,SALT,STATUS,TEAM_ID) values(1,'admin','Admin','admin@springside.org.cn','691b14d79bf0fa2215f155235df5e670b64394cc','7efbd59d9741d34f','enabled',1);
insert into SS_USER (ID,LOGIN_NAME,NAME,EMAIL,PASSWORD,SALT,STATUS,TEAM_ID) values(2,'user','Calvin','user@springside.org.cn','2488aa0c31c624687bd9928e0a5d29e7d1ed520b','6d65d24122c30500','enabled',1);
insert into SS_USER (ID,LOGIN_NAME,NAME,EMAIL,PASSWORD,SALT,STATUS,TEAM_ID) values(3,'user2','Jack','jack@springside.org.cn','2488aa0c31c624687bd9928e0a5d29e7d1ed520b','6d65d24122c30500','enabled',1);
insert into SS_USER (ID,LOGIN_NAME,NAME,EMAIL,PASSWORD,SALT,STATUS,TEAM_ID) values(4,'user3','Kate','kate@springside.org.cn','2488aa0c31c624687bd9928e0a5d29e7d1ed520b','6d65d24122c30500','enabled',1);
insert into SS_USER (ID,LOGIN_NAME,NAME,EMAIL,PASSWORD,SALT,STATUS,TEAM_ID) values(5,'user4','Sawyer','sawyer@springside.org.cn','2488aa0c31c624687bd9928e0a5d29e7d1ed520b','6d65d24122c30500','enabled',1);
insert into SS_USER (ID,LOGIN_NAME,NAME,EMAIL,PASSWORD,SALT,STATUS,TEAM_ID) values(6,'user5','Ben','ben@springside.org.cn','2488aa0c31c624687bd9928e0a5d29e7d1ed520b','6d65d24122c30500','enabled',1);

insert into SS_ROLE (ID,NAME,PERMISSIONS) values(1,'Admin','user:view,user:edit');
insert into SS_ROLE (ID,NAME,PERMISSIONS) values(2,'User','user:view');

insert into SS_USER_ROLE (USER_ID,ROLE_ID) values(1,1);
insert into SS_USER_ROLE (USER_ID,ROLE_ID) values(1,2);
insert into SS_USER_ROLE (USER_ID,ROLE_ID) values(2,2);
insert into SS_USER_ROLE (USER_ID,ROLE_ID) values(3,2);
insert into SS_USER_ROLE (USER_ID,ROLE_ID) values(4,2);
insert into SS_USER_ROLE (USER_ID,ROLE_ID) values(5,2);
insert into SS_USER_ROLE (USER_ID,ROLE_ID) values(6,2);

insert into SS_TEAM (ID,NAME,MASTER_ID) values(1,'Dolphin',1);