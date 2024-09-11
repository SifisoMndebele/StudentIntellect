
drop table if exists storage.folders;
drop table if exists storage.documents;
drop table if exists storage.memoranda;


create table if not exists storage.folders(
    id bigint primary key generated always as identity,
    name text not null,
    module_id int not null,
    parent_id bigint default null,
    creator_id uuid,
    created_at timestamp not null default ext.nowsast(),
    updater_id uuid default null,
    updated_at timestamp default null,
    deleter_id uuid default null,
    deleted_at timestamp default null,
    is_verified boolean not null default false,
    is_public boolean not null default true,
    color char(6) not null default 'f8d775',

    foreign key (module_id) references education.modules(id) on delete cascade on update cascade,
    foreign key (parent_id) references storage.folders(id) on delete cascade on update cascade,
    foreign key (creator_id) references auth.users(id) on delete set null on update cascade,
    foreign key (updater_id) references auth.users(id) on delete set null on update cascade,
    foreign key (deleter_id) references auth.users(id) on delete set null on update cascade
);

create table if not exists storage.documents(
    id bigint primary key generated always as identity,
    name text not null,
    data bytea not null,
    size text not null,
    thumbnail bytea default null,
    downloads bigint not null,
    cost smallint not null default 0,

    folder_id bigint not null,
    creator_id uuid not null,
    created_at timestamp default ext.nowsast() not null,
    updater_id uuid default null,
    updated_at timestamp default null,
    deleter_id uuid default null,
    deleted_at timestamp default null,
    is_verified boolean not null default false,
    is_public boolean not null default true,

    foreign key (folder_id) references storage.folders(id) on delete cascade on update cascade,
    foreign key (creator_id) references auth.users(id) on delete set null on update cascade,
    foreign key (updater_id) references auth.users(id) on delete set null on update cascade,
    foreign key (deleter_id) references auth.users(id) on delete set null on update cascade
);

create table if not exists storage.memoranda(
    id bigint primary key,
    name text not null,
    data bytea not null,
    size text not null,
    thumbnail bytea default null,
    downloads bigint not null,
    cost smallint not null default 0,

    creator_id uuid not null,
    created_at timestamp default ext.nowsast() not null,
    updater_id uuid default null,
    updated_at timestamp default null,
    deleter_id uuid default null,
    deleted_at timestamp default null,
    is_verified boolean not null default false,
    is_public boolean not null default true,

    foreign key (id) references storage.documents(id) on delete cascade on update cascade,
    foreign key (creator_id) references auth.users(id) on delete set null on update cascade,
    foreign key (updater_id) references auth.users(id) on delete set null on update cascade,
    foreign key (deleter_id) references auth.users(id) on delete set null on update cascade
);