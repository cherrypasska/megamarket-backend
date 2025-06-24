create table if not exists products (
    id          bigint              primary key,
    name        varchar(255)        not null default '',
    price       double precision    not null default 0.0,
    quantity    bigint              not null default 0,
    sale        real                not null default 0.0
);