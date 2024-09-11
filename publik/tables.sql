
drop table if exists public.features;
drop table if exists public.feedbacks;


create table if not exists public.features (
    id int primary key generated always as identity,
    name text not null unique,
    module text not null unique,
    clazz text not null default 'MainActivity',
    icon bytea not null,
    created_at timestamp default ext.nowsast() not null,
    parent_id int default null,

    foreign key (parent_id) references public.features(id) on delete cascade on update cascade
);

create table if not exists public.feedbacks (
    id int primary key generated always as identity,
    subject text not null,
    message text not null,
    suggestion text not null default '',
    image bytea default null,
    user_email text default null,
    created_at timestamp default ext.nowsast() not null,

    foreign key (user_email) references auth.users(email) on delete set null on update cascade,
    unique (subject, message, suggestion)
);

