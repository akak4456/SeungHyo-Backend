INSERT INTO problem
(
    problem_no,
    problem_title,
    problem_explain,
    problem_input_explain,
    problem_output_explain,
    is_gradable
) VALUES
    (
        1,
        'A+B',
        'A+B를 구하세요',
        'A와 B가 주어집니다',
        'A+B를 출력하세요',
        'N'
    );

INSERT INTO program_language
VALUES ('JAVA_11', '자바11', 'Y');

insert into problem_condition (condition_no, condition_time, condition_memory, problem_no, lang_code) values (1, 10, 128, 1, 'JAVA_11');

insert into problem_tag
values(1,'성공','#5CB85C');
insert into problem_tag
values(2,'다국어','#777777');

insert into p_pt_correlation (correlation_id, problem_no, tag_no) values (1, 1, 1);
insert into p_pt_correlation (correlation_id, problem_no, tag_no) values (2, 1, 2);

insert into p_pl_correlation (correlation_id, problem_no, lang_code) values (1, 1, 'JAVA_11');

insert into problem_input (input_no, is_example, problem_no, input_source) values (1, 'Y', 1, '1 2');
insert into problem_input (input_no, is_example, problem_no, input_source) values (2, 'Y', 1, '3 4');
insert into problem_input (input_no, is_example, problem_no, input_source) values (3, 'Y', 1, '5 6');
insert into problem_input (input_no, is_example, problem_no, input_source) values (4, 'Y', 1, '7 8');
insert into problem_input (input_no, is_example, problem_no, input_source) values (5, 'Y', 1, '0 0');
insert into problem_input (input_no, is_example, problem_no, input_source) values (6, 'Y', 1, '9 9');

insert into problem_output (output_no, is_example, problem_no, output_source) values (1, 'Y', 1, '3');
insert into problem_output (output_no, is_example, problem_no, output_source) values (2, 'Y', 1, '7');
insert into problem_output (output_no, is_example, problem_no, output_source) values (3, 'Y', 1, '11');
insert into problem_output (output_no, is_example, problem_no, output_source) values (4, 'Y', 1, '15');
insert into problem_output (output_no, is_example, problem_no, output_source) values (5, 'Y', 1, '0');
insert into problem_output (output_no, is_example, problem_no, output_source) values (6, 'Y', 1, '18');

INSERT INTO algorithm_category
VALUES (1, '단순구현');
INSERT INTO algorithm_category
VALUES (2, '복합구현');
INSERT INTO algorithm_category
VALUES (3, 'DP');

INSERT INTO p_pa_correlation
VALUES (1,1,1);
INSERT INTO p_pa_correlation
VALUES (2,1,2);
INSERT INTO p_pa_correlation
VALUES (3,1,3);