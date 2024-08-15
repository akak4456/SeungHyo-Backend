DROP SEQUENCE seq_board;
DROP SEQUENCE seq_reply;
DROP TABLE board_category CASCADE CONSTRAINTS;
DROP TABLE board CASCADE CONSTRAINTS;
DROP TABLE reply CASCADE CONSTRAINTS;

CREATE TABLE board_category(
    category_code VARCHAR2(255) NOT NULL,
    category_name VARCHAR2(255) NOT NULL,
    is_for_admin VARCHAR2(255) NOT NULL,
    PRIMARY KEY (category_code)
);

CREATE SEQUENCE seq_board
    NOCACHE
    NOCYCLE;
CREATE TABLE board(
    board_no NUMBER NOT NULL PRIMARY KEY,
    member_id VARCHAR2(255) NOT NULL,
    board_title VARCHAR2(1000) NOT NULL,
    category_code VARCHAR2(255) NOT NULL,
    lang_code VARCHAR2(255) NOT NULL,
    lang_name VARCHAR2(255) NOT NULL,
    like_count NUMBER DEFAULT 0 NOT NULL,
    reg_date TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    problem_no NUMBER NOT NULL,
    problem_title VARCHAR(1000) NOT NULL,
    board_content CLOB NOT NULL,
    source_code CLOB
);

CREATE SEQUENCE seq_reply
    NOCACHE
    NOCYCLE;
CREATE TABLE reply(
    reply_no NUMBER NOT NULL PRIMARY KEY,
    board_no NUMBER NOT NULL REFERENCES board(board_no),
    member_id VARCHAR2(255) NOT NULL,
    like_count NUMBER NOT NULL,
    reply_content CLOB NOT NULL,
    source_code CLOB,
    reg_date TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL
);