create table scrapper.user_settings(
    user_id bigint not null,
    notify_mood varchar(11),
    notify_time time,
    primary key (user_id),
    foreign key (user_id) references scrapper."user"(chat_id) on delete cascade
);
