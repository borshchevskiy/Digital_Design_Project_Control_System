INSERT INTO employees (firstname, lastname, patronymic, position, email, status)
VALUES ('Ivan', 'Ivanov', 'Ivanovich', 'Developer', 'ivan@gmail.com', 'ACTIVE'),
       ('Petr', 'Petrov', 'Petrovich', 'Tester', 'petr@gmail.com', 'ACTIVE'),
       ('Anton', 'Antonov', 'Antonovich', 'Manager', 'anton@gmail.com', 'ACTIVE');



-- CREATE UNIQUE INDEX unique_active_username ON employees (account) WHERE (status = 'ACTIVE');
--
-- ALTER TABLE employees DROP CONSTRAINT employees_account_key;

INSERT INTO employees (firstname, lastname, patronymic, position, account_id, email, status)
VALUES ('Vlad', 'Smirnov', 'Petrovich', 'Developer', 1, 'petrovich@gmail.com', 'ACTIVE');


INSERT INTO projects (code, name, description, status)
VALUES ('Project-1', 'First project', 'Very-very good project', 'DEVELOPMENT'),
       ('Project-2', 'Second project', 'Very-very boring project', 'TESTING');

INSERT INTO teams (project_id)
VALUES ((SELECT id
         FROM projects AS p
         WHERE p.code like 'Project-1')),
       ((SELECT id
         FROM projects AS p
         WHERE p.code = 'Project-2'));

INSERT INTO team_members (team_id, employee_id, project_role)
VALUES (1, 1, 'DEVELOPER'),
       (1, 3, 'PROJECT_MANAGER'),
       (2, 2, 'TESTER'),
       (2, 3, 'PROJECT_MANAGER');


INSERT INTO tasks (name, description, implementer_id, labor_costs, deadline, status, author_id, date_created,
                   date_updated, project_id)
VALUES ('API development', 'Develop API functionality', 1, 100, '2023-12-31', 'IN_WORK', 3, '2023-05-01', null,
        1),
       ('Repository development', 'Develop repository functionality', 1, 100, '2023-12-31', 'IN_WORK', 3,
        '2023-05-02', null, 1),
       ('API testing', 'Test API', 2, 100, '2023-12-31', 'IN_WORK', 3, '2023-05-03', null, 2),
       ('Security testing', 'Test security', 2, 100, '2023-12-31', 'IN_WORK', 3, '2023-05-04', null, 2);



DROP TABLE tasks;