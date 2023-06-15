package ru.borshchevskiy.pcs.dto.employee.filter;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Фильтр сотрудников")
public record EmployeeFilter(@Schema(description = "Текстовое значение") String value) {
}
