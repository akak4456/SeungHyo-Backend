USE member_service;
INSERT INTO member(member_id, membeR_pw, status_message, delete_yn, email)
VALUES ('user1', 'pass1', 'status1', 'N', 'email1');
INSERT INTO member_roles(member_id, roles)
VALUES ('user1', 'ADMIN');
INSERT INTO member(member_id, membeR_pw, status_message, delete_yn, email)
VALUES ('user2', 'pass2', 'status1', 'N', 'email2');
INSERT INTO member_roles(member_id, roles)
VALUES ('user2', 'ADMIN');
INSERT INTO member(member_id, membeR_pw, status_message, delete_yn, email)
VALUES ('user3', 'pass3', 'status1', 'N', 'email3');
INSERT INTO member_roles(member_id, roles)
VALUES ('user3', 'ADMIN');
INSERT INTO member(member_id, membeR_pw, status_message, delete_yn, email)
VALUES ('user4', 'pass4', 'status1', 'N', 'email4');
INSERT INTO member_roles(member_id, roles)
VALUES ('user4', 'ADMIN');
INSERT INTO member(member_id, membeR_pw, status_message, delete_yn, email)
VALUES ('user5', 'pass5', 'status1', 'N', 'email5');
INSERT INTO member_roles(member_id, roles)
VALUES ('user5', 'ADMIN');