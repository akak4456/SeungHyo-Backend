CREATE TABLE member (
    delete_yn VARCHAR(255) DEFAULT 'N',
    email VARCHAR(255),
    member_id VARCHAR(255) NOT NULL,
    member_pw VARCHAR(255),
    status_message VARCHAR(255),
    PRIMARY KEY (member_id)
);
CREATE TABLE member_roles (
    member_id VARCHAR(255) NOT NULL,
    roles VARCHAR(255)
) engine=InnoDB;
ALTER TABLE member_roles
    ADD CONSTRAINT fk_member_id
    FOREIGN KEY (member_id)
    REFERENCES member (member_id);
INSERT INTO member(MEMBER_ID, MEMBER_PW, STATUS_MESSAGE, DELETE_YN, EMAIL)
VALUES ('user1', 'pass1', 'status1', 'N', 'email1');
INSERT INTO member_roles(MEMBER_ID, ROLES)
VALUES ('user1', 'ADMIN');