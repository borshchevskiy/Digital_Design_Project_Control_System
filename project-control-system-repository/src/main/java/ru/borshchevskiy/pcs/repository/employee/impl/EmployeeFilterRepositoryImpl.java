package ru.borshchevskiy.pcs.repository.employee.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import ru.borshchevskiy.pcs.dto.employee.EmployeeFilter;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.repository.employee.EmployeeFilterRepository;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class EmployeeFilterRepositoryImpl implements EmployeeFilterRepository {

    private final EntityManager entityManager;

    /*
    Поиск сотрудников. Поиск осуществляется по текстовому значению, которое проверяется по атрибутам
    Фамилия, Имя, Отчество, учетной записи, адресу электронной почты и только среди активных сотрудников.
     */

    @Override
    public List<Employee> findAllByFilter(EmployeeFilter filter) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Employee> query = criteriaBuilder.createQuery(Employee.class);
        Root<Employee> employee = query.from(Employee.class);

        // Предикат для поиска по аттрибутам
        List<Predicate> predicates = new ArrayList<>();
        String searchValue = "%" + filter.value() + "%";
        if (filter.value() != null) {
            predicates.add(criteriaBuilder.like(employee.get("firstname"), searchValue));
            predicates.add(criteriaBuilder.like(employee.get("lastname"), searchValue));
            predicates.add(criteriaBuilder.like(employee.get("patronymic"), searchValue));
            predicates.add(criteriaBuilder.like(employee.get("account"), searchValue));
            predicates.add(criteriaBuilder.like(employee.get("email"), searchValue));
        }

        Predicate attributeLikeSearchValue = criteriaBuilder.or(predicates.toArray(Predicate[]::new));

        // Предикат фильтра по статусу ACTIVE
        Predicate statusEqualsActive = criteriaBuilder.equal(employee.get("status").as(String.class), "ACTIVE");


        query.select(employee).where(criteriaBuilder.and(statusEqualsActive, attributeLikeSearchValue));


        return entityManager.createQuery(query).getResultList();
    }
}
