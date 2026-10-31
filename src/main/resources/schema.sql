drop table if exists user_roles;
drop table if exists roles;
drop table if exists users;

create table roles (
    id bigint generated always as identity primary key,
    code varchar(30) not null unique,
    name varchar(30) not null unique,
    description varchar(250),
    enabled boolean not null default true,
    -- Timestamps
    created_at timestamp with time zone default current_timestamp,
    updated_at timestamp with time zone default current_timestamp
);

create index idx_roles_code on roles(code);
create index idx_roles_name on roles(name);

create table users (
    id bigint generated always as identity primary key,
    username varchar(30) not null unique,
    password varchar(255) not null, -- Bcrypt hashed passwords
    enabled boolean not null default true,
    authorities varchar(255),
    -- Timestamps
    created_at timestamp with time zone default current_timestamp,
    updated_at timestamp with time zone default current_timestamp
);

create index idx_users_username on users(username);

create table user_roles (
    id bigint generated always as identity primary key,
    user_id bigint not null,
    role_id bigint not null,
    enabled boolean not null default true,
    -- Timestamps
    created_at timestamp with time zone default current_timestamp,
    updated_at timestamp with time zone default current_timestamp,
    constraint fk_user_roles_user foreign key (user_id) references users(id) on delete cascade,
    constraint fk_user_roles_role foreign key (role_id) references roles(id) on delete cascade,
    constraint uq_user_roles unique (user_id, role_id)
);

create index idx_user_roles_user_id on user_roles(user_id);
create index idx_user_roles_role_id on user_roles(role_id);