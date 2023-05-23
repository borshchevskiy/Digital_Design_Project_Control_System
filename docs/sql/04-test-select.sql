SELECT firstname, lastname
FROM employees
WHERE status = 'ACTIVE'
  AND (firstname like '%ov%'
    OR lastname like '%ov%'
    OR patronymic like '%ov%');


-- SELECT all tasks for project
SELECT name, description, deadline
FROM tasks
WHERE tasks.project_id = 2;


-- SELECT all employees for project
SELECT firstname, lastname, position
FROM employees
WHERE employees.id IN (SELECT employee_id
                       FROM team_members
                       WHERE team_id = (SELECT id
                                        FROM teams
                                        WHERE teams.project_id = 1));

-- SELECT all tasks for team
SELECT name, description, deadline
FROM tasks
WHERE tasks.implementer_id IN (SELECT employee_id
                               FROM team_members
                               WHERE team_id = 1);

-- SELECT projects-tasks-implementer name
SELECT p.name AS project, ts.name AS task, e.firstname AS firstname, e.lastname AS lastname
FROM projects AS p
         JOIN teams AS t on p.id = t.project_id
         JOIN team_members AS tm on t.id = tm.team_id
         JOIN employees AS e on e.id = tm.employee_id
         JOIN tasks AS ts on e.id = ts.implementer_id;

-- SELECT employees tasks
SELECT e.firstname AS firstname, e.lastname AS lastname, p.name AS project_name, t.name AS task
FROM employees e
         JOIN tasks t on e.id = t.implementer_id
         JOIN projects p on t.project_id = p.id;
-- FROM tasks AS t
--          JOIN employees AS e on e.id = t.implementer_id
--          JOIN projects AS p on t.project_id = p.id;


-- Search for 'gmail'
SELECT id,
       firstname,
       lastname,
       patronymic,
       position,
       account,
       email,
       status
FROM employees
WHERE status = 'ACTIVE'
  AND (firstname LIKE '%gmail%' OR lastname LIKE '%gmail%' OR patronymic LIKE '%gmail%' OR account LIKE '%gmail%' OR
       email LIKE '%gmail%');
