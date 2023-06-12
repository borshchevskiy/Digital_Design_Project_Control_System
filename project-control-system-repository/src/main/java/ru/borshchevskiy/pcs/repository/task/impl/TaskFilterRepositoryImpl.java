package ru.borshchevskiy.pcs.repository.task.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import ru.borshchevskiy.pcs.dto.task.TaskFilter;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.entities.task.Task;
import ru.borshchevskiy.pcs.repository.task.TaskFilterRepository;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class TaskFilterRepositoryImpl implements TaskFilterRepository {

    private final EntityManager entityManager;

    /*
    Поиск задач - задачи должны искать по текстовому значению (по полям Наименование задачи) и с применением фильтров
    (по статусам задачи, по исполнителю, по автору задачи, по периоду крайнего срока задачи, по периоду создания задачи).
    Фильтры все не обязательны, как и текстовое поле. Результат должен быть отсортирован
    по дате создания задачи в обратном порядке (сначала свежие задачи).
    */
    @Override
    public List<Task> findAllByFilter(TaskFilter filter) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Task> query = criteriaBuilder.createQuery(Task.class);
        Root<Task> task = query.from(Task.class);

        Predicate taskNameLike = null;
        Predicate attributeFilter = null;

        // Опеределяем предикат taskNameLike (task.name like %%) для поиска по наименованию задачи
        if (filter.name() != null) {
            String nameSearchValue = "%" + filter.name() + "%";
            taskNameLike = criteriaBuilder.like(task.get("name"), nameSearchValue);
        }

        // Опеределяем предикат attributeFilter для фильтра по аттрибутам
        List<Predicate> attributesPredicates = new ArrayList<>();

        if (filter.status() != null) {
            attributesPredicates.add(criteriaBuilder.equal(task.get("status").as(String.class), filter.status().name()));
        }
        if (filter.implementerLastname() != null) {
            Join<Task, Employee> implementer = task.join("implementer");
            attributesPredicates.add(criteriaBuilder.like(implementer.get("lastname"), "%" + filter.implementerLastname() + "%"));
        }
        if (filter.authorLastname() != null) {
            Join<Task, Employee> author = task.join("author");
            attributesPredicates.add(criteriaBuilder.like(author.get("lastname"), "%" + filter.authorLastname() + "%"));
        }
        if (filter.deadline() != null) {
            attributesPredicates.add(criteriaBuilder.lessThan(task.get("deadline"), filter.deadline()));
        }
        if (filter.dateCreated() != null) {
            attributesPredicates.add(criteriaBuilder.greaterThan(task.get("dateCreated"), filter.dateCreated()));
        }


        if (!attributesPredicates.isEmpty()) {
            attributeFilter = criteriaBuilder.or(attributesPredicates.toArray(Predicate[]::new));
        }

        // Определяем условие where. Т.к. все фильтры не обязательны, where может не содержать условий.
        Predicate where = null;

        if (taskNameLike != null && attributeFilter != null) {
            where = criteriaBuilder.and(taskNameLike, attributeFilter);
        } else if (taskNameLike != null) {
            where = taskNameLike;
        } else if (attributeFilter != null) {
            where = attributeFilter;
        }

        // Если where пустой не добавляем его в запрос, получаем просто SELECT * с ORDER BY
        query.select(task);

        if (where != null) {
            query.where(where);
        }

        query.orderBy(criteriaBuilder.desc(task.get("dateCreated")));

        return entityManager.createQuery(query).getResultList();
    }
}
