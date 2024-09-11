drop table if exists education.universities;
drop table if exists education.faculties;
drop table if exists education.schools;
drop table if exists education.departments;
drop table if exists education.modules;
drop table if exists education.users_modules;


create table if not exists education.universities (
    id int primary key generated always as identity,
    name text not null unique,
    province text not null
);

create table if not exists education.faculties(
     id int primary key generated always as identity,
     name text not null unique
);

create table if not exists education.schools(
    id int primary key generated always as identity not null,
    name text not null unique,
    faculty_id int not null,

    foreign key (faculty_id) references education.faculties(id) on delete cascade on update cascade
);

create table if not exists education.departments(
    id int primary key generated always as identity not null,
    name text not null unique,
    school_id int not null unique,

    foreign key (school_id) references education.schools(id) on delete cascade on update cascade
);

create table if not exists education.modules(
    id int primary key generated always as identity,
    code varchar(16) not null unique,
    name text not null,
    department_id int not null,
    university_id int not null,
    image bytea default null,
    creator_id uuid default null,
    created_at timestamp not null default ext.nowsast(),
    updated_at timestamp default null,
    deleted_at timestamp default null,
    is_verified boolean not null default false,

    foreign key (department_id) references education.departments(id) on delete cascade on update cascade,
    foreign key (university_id) references education.universities(id) on delete cascade on update cascade,
    foreign key (creator_id) references auth.users(id) on delete set null on update cascade
);

create table if not exists education.users_modules(
    user_id uuid not null,
    module_id int not null,
    created_at timestamp not null default ext.nowsast(),

    primary key (user_id, module_id),
    foreign key (user_id) references auth.users(id) on delete cascade on update cascade,
    foreign key (module_id) references education.modules(id) on delete cascade on update cascade
);
