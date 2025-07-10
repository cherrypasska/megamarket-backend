create table if not exists orders (
                                      order_id    bigint              primary key,
                                      user_id     bigint              not null default 0,
                                      total_price double precision    not null default 0.0
);

create table if not exists order_products (
                                              id          bigint              primary key,
                                              order_id    bigint              not null,
                                              price       double precision    not null default 0.0,
                                              product_id  bigint              not null default 0,
                                              quantity    bigint              not null default 0,
                                              sale        double precision    not null default 0.0,
                                              foreign key (order_id)          references orders (order_id) on delete cascade on update cascade
);