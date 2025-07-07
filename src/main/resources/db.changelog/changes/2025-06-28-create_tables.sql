--liquibase formatted sql

--changeset pavel11sg:create_users_table
create table users
(
    id         bigserial primary key,
    name       varchar(100)        not null,
    surname    varchar(100)        not null,
    birth_date date           not null check (birth_date > '1900-01-01' and birth_date < current_date),
    email      varchar(255) unique not null check (email like '%@%.%')
);
--rollback drop table users cascade;

--changeset pavel11sg:create_users_indexes
create index idx_users_name_surname on users (name, surname);
create index idx_users_email on users (email);
--rollback drop index if exists idx_users_name_surname;
--rollback drop index if exists idx_users_email;

--changeset pavel11sg:create_card_info_table
create table card_info
(
    id              bigserial primary key,
    user_id         bigint      not null,
    number          varchar(19) not null check (number ~ '^[0-9]{16,19}$'),
    holder          varchar     not null,
    expiration_date date        not null,
    constraint fk_user_id foreign key (user_id) references users (id) on delete cascade
);
--rollback drop table card_info cascade;

--changeset pavel11sg:create_card_info_indexes
create index idx_card_info_user_id on card_info (user_id);
create index idx_card_info_number on card_info (number);
--rollback drop index if exists idx_card_info_user_id;
--rollback drop index if exists idx_card_info_number;