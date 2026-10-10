drop table if exists user;

create table user (
  id bigint primary key auto_increment,
  username varchar(255) not null unique,
  password varchar(255) not null,
  email varchar(255) not null unique,
  created_at timestamp default current_timestamp
);

