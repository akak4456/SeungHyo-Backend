DROP SEQUENCE seq_problem;
DROP SEQUENCE seq_problem_input;
DROP SEQUENCE seq_problem_output;
DROP SEQUENCE seq_problem_condition;
DROP SEQUENCE seq_submt_list;
DROP SEQUENCE seq_problem_grade;
DROP SEQUENCE seq_p_pl_correlation;
DROP SEQUENCE seq_problem_tag;
DROP SEQUENCE seq_p_pt_correlation;
DROP SEQUENCE seq_algorithm_category;
DROP SEQUENCE seq_p_pa_correlation;
DROP TABLE problem CASCADE CONSTRAINTS;
DROP TABLE problem_input CASCADE CONSTRAINTS;
DROP TABLE problem_output CASCADE CONSTRAINTS;
DROP TABLE program_language CASCADE CONSTRAINTS;
DROP TABLE problem_condition CASCADE CONSTRAINTS;
DROP TABLE submit_list CASCADE CONSTRAINTS;
DROP TABLE problem_grade CASCADE CONSTRAINTS;
DROP TABLE p_pl_correlation CASCADE CONSTRAINTS;
DROP TABLE problem_tag CASCADE CONSTRAINTS;
DROP TABLE p_pt_correlation CASCADE CONSTRAINTS;
DROP TABLE algorithm_category CASCADE CONSTRAINTS;
DROP TABLE p_pa_correlation CASCADE CONSTRAINTS;

CREATE SEQUENCE seq_problem
    NOCACHE
    NOCYCLE;
CREATE TABLE problem(
    problem_no NUMBER NOT NULL PRIMARY KEY,
    problem_title VARCHAR2(1000) NOT NULL,
    problem_explain CLOB NOT NULL,
    problem_input_explain CLOB NOT NULL,
    problem_output_explain CLOB NOT NULL,
    IS_GRADABLE VARCHAR2(255) DEFAULT 'N' NOT NULL
);

CREATE SEQUENCE seq_problem_input
    NOCACHE
    NOCYCLE;
CREATE TABLE problem_input(
    input_no NUMBER NOT NULL PRIMARY KEY,
    is_example VARCHAR2(255) DEFAULT 'N' NOT NULL,
    problem_no NUMBER NOT NULL REFERENCES problem(problem_no),
    input_source CLOB NOT NULL
);

CREATE SEQUENCE seq_problem_output
    NOCACHE
    NOCYCLE;
CREATE TABLE problem_output(
    output_no NUMBER NOT NULL PRIMARY KEY,
    is_example VARCHAR2(255) DEFAULT 'N' NOT NULL,
    problem_no NUMBER NOT NULL REFERENCES problem(problem_no),
    output_source CLOB NOT NULL
);

CREATE TABLE program_language(
    lang_code VARCHAR2(255) NOT NULL,
    lang_name VARCHAR2(255) NOT NULL,
    is_gradable VARCHAR2(255) DEFAULT 'N' NOT NULL,
    PRIMARY KEY(lang_code)
);

CREATE SEQUENCE seq_problem_condition
    NOCACHE
    NOCYCLE;
CREATE TABLE problem_condition(
    condition_no NUMBER NOT NULL PRIMARY KEY,
    condition_time DECIMAL(5,2) NOT NULL,
    condition_memory DECIMAL(5,2) NOT NULL,
    problem_no NUMBER NOT NULL REFERENCES problem(problem_no),
    lang_code VARCHAR2(255) NOT NULL REFERENCES program_language(lang_code)
);

CREATE SEQUENCE seq_submt_list
    NOCACHE
    NOCYCLE;
CREATE TABLE submit_list(
    submit_no NUMBER NOT NULL PRIMARY KEY,
    member_id VARCHAR2(255) NOT NULL,
    problem_no NUMBER NOT NULL REFERENCES  problem(problem_no),
    submit_result VARCHAR2(255) NOT NULL,
    max_memory DECIMAL(10,2) NOT NULL,
    max_time DECIMAL(10,2) NOT NULL,
    lang_code VARCHAR2(255) NOT NULL REFERENCES program_language(lang_code),
    submit_date TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    open_range VARCHAR2(255) NOT NULL,
    source_code CLOB NOT NULL
);

CREATE SEQUENCE seq_problem_grade
    NOCACHE
    NOCYCLE;
CREATE TABLE problem_grade(
    grade_no NUMBER NOT NULL PRIMARY KEY,
    grade_result VARCHAR2(255) NOT NULL,
    input_no NUMBER NOT NULL REFERENCES problem_input(input_no),
    output_no NUMBER NOT NULL REFERENCES problem_output(output_no),
    submit_no NUMBER NOT NULL REFERENCES  submit_list(submit_no),
    compile_error_reason VARCHAR2(255),
    runtime_error_reason VARCHAR2(255),
    grade_case_no NUMBER NOT NULL
);

CREATE SEQUENCE seq_p_pl_correlation
    NOCACHE
    NOCYCLE;
CREATE TABLE p_pl_correlation(
    correlation_id NUMBER NOT NULL PRIMARY KEY,
    problem_no NUMBER NOT NULL REFERENCES problem(problem_no),
    lang_code VARCHAR2(255) NOT NULL REFERENCES program_language(lang_code)
);

CREATE SEQUENCE seq_problem_tag
    NOCACHE
    NOCYCLE;
CREATE TABLE problem_tag(
    tag_no NUMBER NOT NULL PRIMARY KEY,
    tag_name VARCHAR2(255) NOT NULL,
    background_color VARCHAR2(255) NOT NULL
);

CREATE SEQUENCE seq_p_pt_correlation
    NOCACHE
    NOCYCLE;
CREATE TABLE p_pt_correlation(
    correlation_id NUMBER NOT NULL PRIMARY KEY,
    problem_no NUMBER NOT NULL REFERENCES problem(problem_no),
    tag_no NUMBER NOT NULL REFERENCES problem_tag(tag_no)
);

CREATE SEQUENCE seq_algorithm_category
    NOCACHE
    NOCYCLE;
CREATE TABLE algorithm_category(
    algorithm_no NUMBER NOT NULL PRIMARY KEY,
    algorithm_name VARCHAR2(255) NOT NULL
);

CREATE SEQUENCE seq_p_pa_correlation
    NOCACHE
    NOCYCLE;
CREATE TABLE p_pa_correlation(
    correlation_id NUMBER NOT NULL PRIMARY KEY,
    problem_no NUMBER NOT NULL REFERENCES problem(problem_no),
    algorithm_no NUMBER NOT NULL REFERENCES algorithm_category(algorithm_no)
);