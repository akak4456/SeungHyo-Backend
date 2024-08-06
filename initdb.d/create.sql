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
    IS_GRADABLE VARCHAR(255) NOT NULL,
    PRIMARY KEY(problem_no)
)