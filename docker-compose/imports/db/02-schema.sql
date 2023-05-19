CREATE TABLE IF NOT EXISTS employees
(
    id BIGSERIAL PRIMARY KEY,
    firstname VARCHAR(255) NOT NULL,
    lastname VARCHAR(255) NOT NULL,
    patronymic VARCHAR(255),
    position VARCHAR(255),
    account VARCHAR(255) UNIQUE,
    email VARCHAR(255) UNIQUE,
    status VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS projects
(
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS teams
(
    id BIGSERIAL PRIMARY KEY,

    project_id BIGINT NOT NULL,
    constraint team_project_fk foreign key (project_id) references projects
);

CREATE TABLE IF NOT EXISTS team_members
(
    id BIGSERIAL PRIMARY KEY,
    team_id BIGINT NOT NULL,
    employee_id BIGINT NOT NULL,
    project_role VARCHAR(255) NOT NULL,
    constraint team_member_team_fk foreign key (team_id) references teams,
    constraint team_member_employee_fk foreign key (employee_id) references employees
);

CREATE TABLE IF NOT EXISTS tasks
(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    implementer_id BIGINT,
    labor_costs BIGINT NOT NULL,
    deadline TIMESTAMP NOT NULL,
    status VARCHAR(255) NOT NULL,
    author_id BIGINT NOT NULL,
    date_created TIMESTAMP NOT NULL,
    date_updated TIMESTAMP,
    project_id BIGINT,
    constraint task_implementer_fk foreign key (implementer_id) references employees,
    constraint task_author_fk foreign key (author_id) references employees,
    constraint task_project_fk foreign key (project_id) references projects
);