package ru.borshchevskiy.pcs.service.services.account;

import ru.borshchevskiy.pcs.dto.account.AccountDto;
import ru.borshchevskiy.pcs.dto.employee.EmployeeDto;

public interface AccountService {


    EmployeeDto save(AccountDto request);


    AccountDto deleteById(AccountDto request);
}
