create table if not exists campsite.availability
(
    id        int auto_increment
        primary key,
    date      date not null,
    available int  not null comment 'Number of available reservations',
    constraint availability_pk2
        unique (date),
    constraint check_available_greater_than
        check (`available` > -(1))
)
    comment 'Contains the availability of reservations by date';


