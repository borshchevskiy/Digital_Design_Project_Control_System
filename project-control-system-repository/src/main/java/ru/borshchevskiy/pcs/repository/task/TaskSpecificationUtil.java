package ru.borshchevskiy.pcs.repository.task;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import ru.borshchevskiy.pcs.dto.task.TaskFilter;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.entities.task.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskSpecificationUtil {
    public static Specification<Task> getSpecification(TaskFilter filter) {

        /*
        Поиск задач - задачи должны искать по текстовому значению (по полям Наименование задачи) и с применением фильтров
        (по статусам задачи, по исполнителю, по автору задачи, по периоду крайнего срока задачи, по периоду создания задачи).
        Фильтры все не обязательны, как и текстовое поле. Результат должен быть отсортирован
        по дате создания задачи в обратном порядке (сначала свежие задачи).
        */
        return (root, query, criteriaBuilder) -> {
            Predicate taskNameLike = null;
            Predicate attributeFilter = null;

            // Опеределяем предикат taskNameLike (task.name like %%) для поиска по наименованию задачи
            if (!ObjectUtils.isEmpty(filter.name())) {
                String nameSearchValue = "%" + filter.name() + "%";
                taskNameLike = criteriaBuilder.like(root.get("name"), nameSearchValue);
            }

            // Опеределяем предикат attributeFilter для фильтра по аттрибутам
            List<Predicate> attributesPredicates = new ArrayList<>();

            if (!ObjectUtils.isEmpty(filter.status())) {
                attributesPredicates.add(criteriaBuilder.equal(root.get("status").as(String.class), filter.status().name()));
            }
            if (!ObjectUtils.isEmpty(filter.implementerName())) {
                Join<Task, Employee> implementer = root.join("implementer");
                attributesPredicates.add(criteriaBuilder.like(implementer.get("lastname"), "%" + filter.implementerName() + "%"));
            }
            if (!ObjectUtils.isEmpty(filter.authorName())) {
                Join<Task, Employee> author = root.join("author");
                attributesPredicates.add(criteriaBuilder.like(author.get("lastname"), "%" + filter.authorName() + "%"));
            }
            if (!ObjectUtils.isEmpty(filter.deadline())) {
                attributesPredicates.add(criteriaBuilder.lessThan(root.get("deadline"), filter.deadline()));
            }
            if (!ObjectUtils.isEmpty(filter.dateCreated())) {
                attributesPredicates.add(criteriaBuilder.greaterThan(root.get("dateCreated"), filter.dateCreated()));
            }


            if (!CollectionUtils.isEmpty(attributesPredicates)) {
                attributeFilter = criteriaBuilder.or(attributesPredicates.toArray(Predicate[]::new));
            }


            // Определяем условие where. Т.к. все фильтры не обязательны, where может не содержать условий.
            Predicate where = null;

            if (!ObjectUtils.isEmpty(taskNameLike) && !ObjectUtils.isEmpty(attributeFilter)) {
                where = criteriaBuilder.and(taskNameLike, attributeFilter);

            } else if (taskNameLike != null) {
                where = taskNameLike;

            } else if (attributeFilter != null) {
                where = attributeFilter;
            }


            // Если where пустой не добавляем его в запрос, получаем просто SELECT * с ORDER BY
            if (!ObjectUtils.isEmpty(where)) {
                return query.where(where).orderBy(criteriaBuilder.desc(root.get("dateCreated"))).getRestriction();
            }
            return query.orderBy(criteriaBuilder.desc(root.get("dateCreated"))).getRestriction();
        };
    }
}
