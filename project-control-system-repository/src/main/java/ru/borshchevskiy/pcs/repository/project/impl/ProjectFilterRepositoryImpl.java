package ru.borshchevskiy.pcs.repository.project.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import ru.borshchevskiy.pcs.dto.project.ProjectFilter;
import ru.borshchevskiy.pcs.entities.project.Project;
import ru.borshchevskiy.pcs.enums.ProjectStatus;
import ru.borshchevskiy.pcs.repository.project.ProjectFilterRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class ProjectFilterRepositoryImpl implements ProjectFilterRepository {

    private final EntityManager entityManager;

    /*
    Поиск проектов. Поиск должен осуществляться по текстовому значению (по полям Наименование проекта, Код проекта)
    и с применением фильтров по Статусу проекта. Т.е. на вход передается некоторое текстовое значение и список статусов.
     */
    @Override
    public List<Project> findAllByFilter(ProjectFilter filter) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Project> query = criteriaBuilder.createQuery(Project.class);
        Root<Project> project = query.from(Project.class);

        // Предикат для поиска по текстовому значению
        List<Predicate> predicates = new ArrayList<>();
        String searchValue = "%" + filter.value() + "%";

        if (filter.value() != null) {
            predicates.add(criteriaBuilder.like(project.get("name"), searchValue));
            predicates.add(criteriaBuilder.like(project.get("code"), searchValue));
        }
        Predicate attributeLikeSearchValue = criteriaBuilder.or(predicates.toArray(Predicate[]::new));

        // Предикат для фильтрации статусов
        List<ProjectStatus> statuses = filter.statuses();
        Predicate statusEquals = criteriaBuilder.or(statuses.stream()
                .map(s -> criteriaBuilder.equal(project.get("status").as(String.class), s.name()))
                .toList().toArray(Predicate[]::new));

        // Query с итоговым WHERE
        query.select(project).where(criteriaBuilder.and(statusEquals, attributeLikeSearchValue));


        return entityManager.createQuery(query).getResultList();

    }

}
