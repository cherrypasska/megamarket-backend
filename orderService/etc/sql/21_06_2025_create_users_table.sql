create table if not exists users (
    id_users        bigint          primary key,
    username        varchar(255)    not null default '',
    email           varchar(255)    not null default '',
    user_password   varchar(255)    not null default '',
    user_role       varchar(255)    not null default 'ROLE_USER'
);