PRAGMA foreign_keys=OFF;
BEGIN TRANSACTION;
CREATE TABLE card_history (
  package_id int(11) NOT NULL,
  card_pk int(11) NOT NULL,
  old_name varchar(45) NOT NULL DEFAULT '' ,
  new_name varchar(45) NOT NULL DEFAULT '' ,
  rare varchar(45) NOT NULL,
  db_created_time timestamp default (strftime('%Y-%m-%d %H:%M:%f','now','localtime'))
);
CREATE TABLE package_card (
  card_pk INTEGER PRIMARY KEY AUTOINCREMENT,
  card_name varchar(200) NOT NULL DEFAULT '' ,
  package_id int(10)  NOT NULL DEFAULT '0' ,
  rare varchar(45) NOT NULL DEFAULT 'N' ,
  db_created_time timestamp default (strftime('%Y-%m-%d %H:%M:%f','now','localtime'))
);
CREATE TABLE package_info (
  package_id INTEGER PRIMARY KEY AUTOINCREMENT,
  package_name varchar(45) NOT NULL DEFAULT '' ,
  db_created_time timestamp default (strftime('%Y-%m-%d %H:%M:%f','now','localtime'))
);
CREATE TABLE roll_detail (
  roll_id bigint(20) NOT NULL ,
  card_pk int(11) NOT NULL ,
  is_dust tinyint(4) NOT NULL ,
  db_created_time timestamp default (strftime('%Y-%m-%d %H:%M:%f','now','localtime'))
);
CREATE TABLE roll_list (
  roll_id INTEGER PRIMARY KEY AUTOINCREMENT,
  roll_user_id int(11) NOT NULL DEFAULT '0' ,
  roll_package_id int(11) NOT NULL DEFAULT '0' ,
  is_disabled tinyint(4) NOT NULL DEFAULT '0' ,
  db_created_time timestamp default (strftime('%Y-%m-%d %H:%M:%f','now','localtime'))
);
CREATE TABLE user_card_list (
  user_id int(11) NOT NULL ,
  card_pk int(11) NOT NULL DEFAULT '0' ,
  count int(11) NOT NULL DEFAULT '0' ,
  db_created_time timestamp default (strftime('%Y-%m-%d %H:%M:%f','now','localtime'))
);
CREATE TABLE user_data (
  user_id INTEGER PRIMARY KEY AUTOINCREMENT,
  user_name varchar(45) NOT NULL DEFAULT '' ,
  password varchar(100) NOT NULL DEFAULT '' ,
  is_admin tinyint(4) NOT NULL DEFAULT '0' ,
  nonaward_count int(11) NOT NULL DEFAULT '0' ,
  dust_count int(11) NOT NULL DEFAULT '0' ,
  duel_point int NOT NULL DEFAULT '0' ,
  daily_win int NOT NULL DEFAULT '0' ,
  daily_lost int NOT NULL DEFAULT '0' ,
  daily_award int NOT NULL DEFAULT '0' ,
  weekly_dust_change_n int NOT NULL DEFAULT '0' ,
  weekly_dust_change_r int NOT NULL DEFAULT '0' ,
  weekly_dust_change_alter int NOT NULL DEFAULT '0' ,
  db_created_time timestamp default (strftime('%Y-%m-%d %H:%M:%f','now','localtime'))
);
CREATE TABLE record (
  record_id INTEGER PRIMARY KEY AUTOINCREMENT,
  operator varchar(45) NOT NULL DEFAULT '' ,
  detail text NOT NULL DEFAULT '' ,
  db_created_time timestamp default (strftime('%Y-%m-%d %H:%M:%f','now','localtime'))
);
CREATE TABLE collection (
  collection_id INTEGER PRIMARY KEY AUTOINCREMENT,
  user_id int(11) NOT NULL ,
  card_pk int(11) NOT NULL DEFAULT '0' ,
  db_created_time timestamp default (strftime('%Y-%m-%d %H:%M:%f','now','localtime'))
);
INSERT INTO user_data (user_name,password,is_admin,nonaward_count,dust_count,duel_point) VALUES ("admin","e10adc3949ba59abbe56e057f20f883e",1,0,0,0);
DELETE FROM sqlite_sequence;
INSERT INTO sqlite_sequence VALUES('user_data',1);
COMMIT;
