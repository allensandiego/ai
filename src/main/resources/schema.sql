drop table if exists users;

create table users (
    id bigint generated always as identity primary key,
    username varchar(50) not null unique,
    email varchar(100) not null unique,
    password varchar(255) not null,
    created_at timestamp default current_timestamp
);

