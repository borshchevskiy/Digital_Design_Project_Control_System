package ru.borshchevskiy.pcs.repository.employee;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import ru.borshchevskiy.pcs.dto.employee.filter.EmployeeFilter;
import ru.borshchevskiy.pcs.entities.account.Account;
import ru.borshchevskiy.pcs.entities.employee.Employee;

import java.util.ArrayList;
import java.util.List;

@Component
public class EmployeeSpecificationUtil {

    /*
    Поиск сотрудников. Поиск осуществляется по текстовому значению, которое проверяется по атрибутам
    Фамилия, Имя, Отчество, учетной записи, адресу электронной почты и только среди активных сотрудников.
     */
     /*
    Условие формируется по следующему принципу:
    WHERE employee.status = employeestatus.active AND
    (employee.firstname like filter.value
    OR employee.lastname like filter.value
    OR employee.patronymic like filter.value
    OR employee.account.username like filter.value
    OR employee.email like filter.value)

     */
    public static Specification<Employee> getSpecification(EmployeeFilter filter) {
        return (root, query, criteriaBuilder) -> {

            // Предикат условия по статусу ACTIVE
            Predicate statusEqualsActive = criteriaBuilder.equal(root.get("status").as(String.class), "ACTIVE");

            List<Predicate> predicates = new ArrayList<>();

            if (!ObjectUtils.isEmpty(filter.value())) {
                // Предикат условия поиска по атрибутам
                String searchValue = "%" + filter.value() + "%";
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("firstname")) , searchValue.toLowerCase()));
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("lastname")), searchValue.toLowerCase()));
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("patronymic")), searchValue.toLowerCase()));

                Join<Employee, Account> account = root.join("account", JoinType.LEFT);
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(account.get("username")), searchValue.toLowerCase()));

                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), searchValue.toLowerCase()));
                Predicate attributeLikeSearchValue = criteriaBuilder.or(predicates.toArray(Predicate[]::new));
                return query.where(criteriaBuilder.and(statusEqualsActive, attributeLikeSearchValue)).getRestriction();
            } else {
                return query.where(statusEqualsActive).getRestriction();
            }
        };
    }
}
