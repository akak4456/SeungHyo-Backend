create table member (
    delete_yn varchar(255),
    email varchar(255),
    member_id varchar(255) not null,
    member_pw varchar(255),
    status_message varchar(255),
    primary key (member_id)
) engine=InnoDB;
create table member_roles (
    member_id varchar(255) not null,
    roles varchar(255)
) engine=InnoDB;
alter table member_roles
    add constraint FKet63dfllh4o5qa9qwm7f5kx9x
    foreign key (member_id)
    references member (member_id);
INSERT INTO member(MEMBER_ID, MEMBER_PW, STATUS_MESSAGE, DELETE_YN, EMAIL)
VALUES ('user1', 'pass1', 'status1', 'Y', 'email1');
INSERT INTO member_roles(MEMBER_ID, ROLES)
VALUES ('user1', 'ADMIN');