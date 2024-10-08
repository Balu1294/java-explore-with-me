drop table if exists users cascade;
drop table if exists location cascade;
drop table if exists categories cascade;
drop table if exists events cascade;
drop table if exists compilations cascade;
drop table if exists compilations_to_event cascade;
drop table if exists requests cascade;

create table if not exists users
(
    id    integer generated always as identity primary key unique,
    name  varchar(250)        not null,
    email varchar(254) unique not null
    );

create table if not exists categories
(
    id   integer generated always as identity primary key unique,
    name varchar(50) unique not null
    );

create table if not exists location
(
    id    integer generated always as identity primary key unique,
    lat   numeric,
    lon   numeric
);

create table if not exists events
(
    id                 integer generated always as identity primary key unique,
    annotation         varchar(2000)               not null,
    category_id        integer                     not null,
    confirmed_requests integer,
    create_date        timestamp without time zone,
    description        varchar(7000),
    event_date         timestamp without time zone not null,
    initiator_id       integer                      not null,
    location_id        integer,
    paid               boolean,
    participant_limit  integer default 0,
    published_date     timestamp without time zone,
    request_moderation boolean default true,
    status             varchar(200),
    title              varchar(120)                not null,
    constraint fk_event_to_user foreign key (initiator_id) references users (id),
    constraint fk_event_to_category foreign key (category_id) references categories (id),
    constraint fk_location foreign key (location_id) references location (id)
    );

create table if not exists requests
(
    id            integer generated always as identity primary key,
    event_id      integer not null,
    requester_id  integer not null,
    create_date  timestamp without time zone,
    status       varchar(20),
    constraint fk_requests_to_event foreign key (event_id) references events (id),
    constraint fk_requests_to_user foreign key (requester_id) references users (id)
    );

create table if not exists compilations
(
    id      integer generated always as identity primary key unique,
    pinned boolean      not null,
    title  varchar(50) not null
    );

create table if not exists compilations_to_event
(
    id              integer generated always as identity primary key,
    event_id        integer not null,
    compilation_id  integer not null,
    constraint fk_event_compilation_to_event foreign key (event_id) references events (id) on update cascade,
    constraint fk_event_compilation_to_compilation foreign key (compilation_id) references compilations (id) on update cascade
    );