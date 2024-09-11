
-- drop type if exists auth.usertype;
-- create type auth.usertype as enum (
--     'STUDENT',
--     'TUTOR',
--     'EXPERT'
-- );

drop type if exists auth.user_row;
create type auth.user_row as (
    id uuid,
    email text,
    password text,
    names text,
    lastname text,
    image bytea,
    phone text,
    created_at timestamp,
    updated_at timestamp,
    is_email_verified boolean,
    is_student boolean,
--     usertype auth.usertype,
    university text,
    school text,
    course text,
    is_admin boolean,
    is_super_admin boolean,
    admin_since timestamp,
    admin_assigned_by uuid
);