CREATE DATABASE IF NOT EXISTS `member_service`;
CREATE DATABASE IF NOT EXISTS `problem_service`;
CREATE DATABASE IF NOT EXISTS `board_service`;
GRANT ALL PRIVILEGES on *.* TO 'adele'@'%' WITH GRANT OPTION;
USE member_service;
CREATE TABLE member (
    member_id VARCHAR(255) NOT NULL,
    member_pw VARCHAR(500) NOT NULL,
    status_message LONGTEXT NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    delete_yn VARCHAR(10) NOT NULL DEFAULT 'N',
    PRIMARY KEY (member_id)
);
CREATE TABLE member_roles (
    roles VARCHAR(255) NOT NULL,
    member_id VARCHAR(50) NOT NULL REFERENCES member(member_id),
    PRIMARY KEY(member_id, roles)
);
USE problem_service;
CREATE TABLE problem(
    problem_no BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    problem_title VARCHAR(1000) NOT NULL,
    problem_explain LONGTEXT NOT NULL,
    problem_input_explain LONGTEXT NOT NULL,
    problem_output_explain LONGTEXT NOT NULL,
    IS_GRADABLE VARCHAR(10) NOT NULL DEFAULT('N')
);
CREATE TABLE problem_input(
    input_no BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    is_example VARCHAR(10) NOT NULL DEFAULT('N'),
    problem_no BIGINT NOT NULL REFERENCES problem(problem_no),
    input_source LONGTEXT NOT NULL
);
CREATE TABLE problem_output(
    output_no BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    is_example VARCHAR(10) NOT NULL DEFAULT('N'),
    problem_no BIGINT NOT NULL REFERENCES problem(problem_no),
    output_source LONGTEXT NOT NULL
);
CREATE TABLE program_language(
    lang_code VARCHAR(255) NOT NULL,
    lang_name VARCHAR(100) NOT NULL,
    is_gradable VARCHAR(10) NOT NULL DEFAULT('N'),
    PRIMARY KEY(lang_code)
);
CREATE TABLE problem_condition(
    condition_no BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    condition_time DECIMAL(5,2) NOT NULL,
    condition_memory DECIMAL(5,2) NOT NULL,
    problem_no BIGINT NOT NULL REFERENCES problem(problem_no),
    lang_code VARCHAR(255) NOT NULL REFERENCES program_language(lang_code)
);
CREATE TABLE submit_list(
    submit_no BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    member_id VARCHAR(255) NOT NULL,
    problem_no BIGINT NOT NULL REFERENCES  problem(problem_no),
    submit_result VARCHAR(50) NOT NULL,
    max_memory DECIMAL(10,2) NOT NULL,
    max_time DECIMAL(10,2) NOT NULL,
    lang_code VARCHAR(255) NOT NULL REFERENCES program_language(lang_code),
    submit_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    open_range VARCHAR(255) NOT NULL,
    source_code LONGTEXT NOT NULL
);
CREATE TABLE problem_grade(
    grade_no BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    grade_result VARCHAR(50) NOT NULL,
    input_no BIGINT NOT NULL REFERENCES problem_input(input_no),
    output_no BIGINT NOT NULL REFERENCES problem_output(output_no),
    submit_no BIGINT NOT NULL REFERENCES  submit_list(submit_no),
    compile_error_reason VARCHAR(50),
    runtime_error_reason VARCHAR(255),
    grade_case_no BIGINT NOT NULL
);
CREATE TABLE problem_program_language_correlation(
    correlation_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    problem_no BIGINT NOT NULL REFERENCES problem(problem_no),
    lang_code VARCHAR(255) NOT NULL REFERENCES program_language(lang_code)
);
CREATE TABLE problem_tag(
    tag_no BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    tag_name VARCHAR(500) NOT NULL,
    background_color VARCHAR(50) NOT NULL
);
CREATE TABLE problem_problem_tag_correlation(
    correlation_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    problem_no BIGINT NOT NULL REFERENCES problem(problem_no),
    tag_no BIGINT NOT NULL REFERENCES problem_tag(tag_no)
);
CREATE TABLE algorithm_category(
    algorithm_no BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    algorithm_name VARCHAR(500) NOT NULL
);
CREATE TABLE problem_algorithm_category_correlation(
    correlation_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    problem_no BIGINT NOT NULL REFERENCES problem(problem_no),
    algorithm_no BIGINT NOT NULL REFERENCES algorithm_category(algorithm_no)
);
USE board_service;
CREATE TABLE board_category(
    category_code VARCHAR(255) NOT NULL,
    category_name VARCHAR(500) NOT NULL,
    is_for_admin VARCHAR(10) NOT NULL,
    order_num INT NOT NULL,
    PRIMARY KEY (category_code)
);
CREATE TABLE board(
    board_no BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    member_id VARCHAR(255) NOT NULL,
    board_title VARCHAR(1000) NOT NULL,
    category_code VARCHAR(255) NOT NULL,
    lang_code VARCHAR(255) NOT NULL,
    lang_name VARCHAR(100) NOT NULL,
    like_count BIGINT NOT NULL DEFAULT 0,
    reg_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    problem_no BIGINT NOT NULL,
    problem_title VARCHAR(1000) NOT NULL,
    board_content LONGTEXT NOT NULL,
    source_code LONGTEXT
);
CREATE TABLE reply(
    reply_no BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    board_no BIGINT NOT NULL REFERENCES board(board_no),
    member_id VARCHAR(255) NOT NULL,
    like_count BIGINT NOT NULL DEFAULT 0,
    reply_content LONGTEXT NOT NULL,
    source_code LONGTEXT,
    lang_code VARCHAR(255) NOT NULL DEFAULT 'JAVA_11',
    lang_name VARCHAR(100) NOT NULL DEFAULT '자바11',
    reg_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE board_like(
      board_like_no BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
      board_no BIGINT NOT NULL REFERENCES board(board_no),
      member_id VARCHAR(255) NOT NULL
);

CREATE TABLE reply_like(
       reply_like_no BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
       reply_no BIGINT NOT NULL REFERENCES reply(reply_no),
       member_id VARCHAR(255) NOT NULL
);