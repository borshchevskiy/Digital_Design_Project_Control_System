package ru.borshchevskiy.pcs.services.account;

import ru.borshchevskiy.pcs.dto.account.AccountDto;
import ru.borshchevskiy.pcs.dto.employee.EmployeeDto;

public interface AccountService {


    AccountDto save(AccountDto request);
}
