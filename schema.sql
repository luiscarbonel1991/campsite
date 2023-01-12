create table if not exists campsite.availability
(
    id              int auto_increment
        primary key,
    date            date not null,
    available       int  not null comment 'Number of available reservations',
    available_total int  not null comment 'Number of available total reservations',
    constraint availability_pk2
        unique (date),
    constraint check_available_greater_than
        check (`available` > -(1)),
    constraint check_available_total_equal_or_greater_than_available
        check (`available` <= `available_total`),
    constraint check_available_total_greater_than
        check (`available_total` > -(1))
)
    comment 'Contains the availability of reservations by date';


create table if not exists campsite.reservation
(
    id             bigint       not null
        primary key,
    name           varchar(100) not null,
    email          varchar(50)  not null,
    arrival_date   date         not null,
    departure_date date         not null,
    create_date    datetime     not null,
    update_date    datetime     null,
    cancel_date    datetime     null
) comment 'Contains campsite reservations';




