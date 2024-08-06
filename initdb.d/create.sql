CREATE TABLE member (
    delete_yn VARCHAR(255) DEFAULT 'N',
    email VARCHAR(255) NOT NULL UNIQUE,
    member_id VARCHAR(255) NOT NULL,
    member_pw VARCHAR(255) NOT NULL,
    status_message VARCHAR(255) NOT NULL,
    PRIMARY KEY (member_id)
);
CREATE TABLE member_roles (
    member_id VARCHAR(255) NOT NULL REFERENCES member(member_id),
    roles VARCHAR(255) NOT NULL,
    PRIMARY KEY(member_id, roles)
);

CREATE TABLE problem(
    problem_no BIGINT NOT NULL,
    problem_title VARCHAR(255) NOT NULL,
    problem_explain VARCHAR(255) NOT NULL,
    problem_input_explain VARCHAR(255) NOT NULL,
    problem_output_explain VARCHAR(255) NOT NULL,
    IS_GRADABLE VARCHAR(255) NOT NULL DEFAULT('N'),
    PRIMARY KEY(problem_no)
);
CREATE TABLE problem_input(
    input_no BIGINT NOT NULL,
    is_example VARCHAR(255) NOT NULL DEFAULT('N'),
    problem_no BIGINT NOT NULL REFERENCES problem(problem_no),
    input_file_name VARCHAR(255) NOT NULL,
    PRIMARY KEY(input_no)
);
CREATE TABLE problem_output(
    output_no BIGINT NOT NULL,
    is_example VARCHAR(255) NOT NULL DEFAULT('N'),
    problem_no BIGINT NOT NULL REFERENCES problem(problem_no),
    output_file_name VARCHAR(255) NOT NULL,
    PRIMARY KEY(output_no)
);
CREATE TABLE program_language(
    lang_code VARCHAR(255) NOT NULL,
    lang_name VARCHAR(255) NOT NULL,
    is_gradable VARCHAR(255) NOT NULL DEFAULT('N'),
    PRIMARY KEY(lang_code)
);
CREATE TABLE problem_condition(
    condition_no BIGINT NOT NULL,
    condition_time DECIMAL(5,2) NOT NULL,
    condition_memory DECIMAL(5,2) NOT NULL,
    problem_no BIGINT NOT NULL REFERENCES problem(problem_no),
    lang_code VARCHAR(255) NOT NULL REFERENCES program_language(lang_code),
    PRIMARY KEY(condition_no)
);
CREATE TABLE submit_list(
    submit_no BIGINT NOT NULL,
    member_id VARCHAR(255) NOT NULL REFERENCES member(member_id),
    problem_no BIGINT NOT NULL REFERENCES  problem(problem_no),
    submit_result VARCHAR(255) NOT NULL,
    max_memory DECIMAL(10,2) NOT NULL,
    max_time DECIMAL(10,2) NOT NULL,
    lang_code VARCHAR(255) NOT NULL REFERENCES program_language(lang_code),
    submit_date DATETIME NOT NULL,
    open_range VARCHAR(255) NOT NULL,
    source_code LONGTEXT NOT NULL,
    PRIMARY KEY(submit_no)
);
CREATE TABLE problem_grade(
    grade_no BIGINT NOT NULL,
    grade_result VARCHAR(255) NOT NULL,
    input_no BIGINT NOT NULL REFERENCES problem_input(input_no),
    output_no BIGINT NOT NULL REFERENCES problem_output(output_no),
    submit_no BIGINT NOT NULL REFERENCES  submit_list(submit_no),
    grade_case_no BIGINT NOT NULL,
    PRIMARY KEY(grade_no)
);
CREATE TABLE problem_program_language_correlation(
    problem_no BIGINT NOT NULL REFERENCES problem(problem_no),
    lang_code VARCHAR(255) NOT NULL REFERENCES program_language(lang_code),
    PRIMARY KEY (problem_no, lang_code)
);
CREATE TABLE problem_tag(
    tag_name VARCHAR(255) NOT NULL,
    background_color VARCHAR(255) NOT NULL,
    PRIMARY KEY(tag_name)
);