DROP TABLE member CASCADE CONSTRAINTS;
DROP TABLE member_roles CASCADE CONSTRAINTS;

CREATE TABLE member (
    delete_yn VARCHAR2(255) DEFAULT 'N',
    email VARCHAR2(255) NOT NULL UNIQUE,
    member_id VARCHAR2(255) NOT NULL,
    member_pw VARCHAR2(255) NOT NULL,
    status_message VARCHAR2(255) NOT NULL,
    PRIMARY KEY (member_id)
);
CREATE TABLE member_roles (
    member_id VARCHAR2(255) NOT NULL REFERENCES member(member_id),
    roles VARCHAR2(255) NOT NULL,
    PRIMARY KEY(member_id, roles)
);