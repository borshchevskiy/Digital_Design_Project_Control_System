package ru.borshchevskiy.pcs.repository.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.borshchevskiy.pcs.dto.project.ProjectDto;
import ru.borshchevskiy.pcs.entities.project.Project;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long>, ProjectFilterRepository {



}
