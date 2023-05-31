package ru.borshchevskiy.pcs.dto.account;

import lombok.Data;
import ru.borshchevskiy.pcs.common.enums.Role;

import java.util.Set;

@Data
public class AccountDto {

    private Long id;

    private String username;

    private String password;

    private Set<Role> roles;
}
