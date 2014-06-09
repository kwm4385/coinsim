# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table simulation (
  id                        integer primary key AUTOINCREMENT,
  user_id                   integer,
  name                      varchar(255),
  trading_fee               double,
  dollars                   double,
  coins                     double,
  trades                    varchar(255))
;

create table token (
  token                     varchar(255) primary key,
  user_id                   integer,
  type                      varchar(8),
  date_creation             timestamp,
  email                     varchar(255),
  constraint ck_token_type check (type in ('password','email')))
;

create table trade (
  id                        integer primary key AUTOINCREMENT,
  type                      integer,
  amount                    double,
  date                      timestamp,
  constraint ck_trade_type check (type in (0,1)))
;

create table user (
  id                        integer primary key AUTOINCREMENT,
  active_simulation         integer,
  email                     varchar(255),
  confirmation_token        varchar(255),
  password_hash             varchar(255),
  date_creation             timestamp,
  validated                 integer(1),
  constraint uq_user_email unique (email))
;




# --- !Downs

PRAGMA foreign_keys = OFF;

drop table simulation;

drop table token;

drop table trade;

drop table user;

PRAGMA foreign_keys = ON;

