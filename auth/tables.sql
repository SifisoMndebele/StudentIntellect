
drop table if exists auth.users;
drop table if exists auth.admins;
drop table if exists auth.students;
drop table if exists auth.experts;
drop table if exists auth.otp;


create table if not exists auth.users(
    id uuid primary key default ext.gen_random_uuid(),
    email text unique not null,
    password text default null,
    names text not null,
    lastname text not null,
    image bytea default null,
    phone text default null,
    created_at timestamp default ext.nowsast() not null,
    updated_at timestamp default null,
    last_seen_at timestamp default null,
    banned_until timestamp default null,
    deleted_at timestamp default null,
    is_email_verified boolean default false not null
);

create table if not exists auth.admins (
    id uuid primary key,
    assigned_by uuid default null,
    is_super boolean default false not null,
    admin_since timestamp default ext.nowsast() not null,

    foreign key (id) references auth.users(id) on delete cascade on update cascade,
    foreign key (assigned_by) references auth.admins(id) on delete set null on update cascade
);

create table if not exists auth.students (
    id uuid primary key,
    university text not null,
    school text not null,
    course text not null,
    is_verified boolean not null default false,

    foreign key (id) references auth.users(id) on delete cascade on update cascade
);

create table if not exists auth.experts (
    id uuid primary key,
    university text not null,
    school text not null,
    course text not null,
    module_id text not null,
    is_verified boolean not null default false,

    foreign key (id) references auth.users(id) on delete cascade on update cascade
);

create table if not exists auth.otp(
    id integer primary key generated always as identity,
    email text not null unique,
    otp text not null,
    valid_until timestamp default (ext.nowsast() + '5 min'::interval) not null
);