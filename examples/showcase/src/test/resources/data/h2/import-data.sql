insert into ss_user (id, login_name, name, email, password, salt, status, team_id) values(1,'admin','管理员','admin@springside.org.cn','691b14d79bf0fa2215f155235df5e670b64394cc','7efbd59d9741d34f','enabled',1);
insert into ss_user (id, login_name, name, email, password, salt, status, team_id) values(2,'user','Calvin','user@springside.org.cn','2488aa0c31c624687bd9928e0a5d29e7d1ed520b','6d65d24122c30500','enabled',1);
insert into ss_user (id, login_name, name, email, password, salt, status, team_id) values(3,'user2','Jack','jack@springside.org.cn','2488aa0c31c624687bd9928e0a5d29e7d1ed520b','6d65d24122c30500','enabled',1);
insert into ss_user (id, login_name, name, email, password, salt, status, team_id) values(4,'user3','Kate','kate@springside.org.cn','2488aa0c31c624687bd9928e0a5d29e7d1ed520b','6d65d24122c30500','enabled',1);
insert into ss_user (id, login_name, name, email, password, salt, status, team_id) values(5,'user4','Sawyer','sawyer@springside.org.cn','2488aa0c31c624687bd9928e0a5d29e7d1ed520b','6d65d24122c30500','enabled',1);
insert into ss_user (id, login_name, name, email, password, salt, status, team_id) values(6,'user5','Ben','ben@springside.org.cn','2488aa0c31c624687bd9928e0a5d29e7d1ed520b','6d65d24122c30500','enabled',1);

insert into ss_role (id, name, permissions) values(1,'Admin','user:view,user:edit');
insert into ss_role (id, name, permissions) values(2,'User','user:view');

insert into ss_user_role (user_id, role_id) values(1,1);
insert into ss_user_role (user_id, role_id) values(1,2);
insert into ss_user_role (user_id, role_id) values(2,2);
insert into ss_user_role (user_id, role_id) values(3,2);
insert into ss_user_role (user_id, role_id) values(4,2);
insert into ss_user_role (user_id, role_id) values(5,2);
insert into ss_user_role (user_id, role_id) values(6,2);

insert into ss_team (id, name, master_id) values(1,'Dolphin',1);