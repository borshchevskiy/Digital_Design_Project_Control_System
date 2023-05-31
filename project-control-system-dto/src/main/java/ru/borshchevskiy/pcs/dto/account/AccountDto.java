package ru.borshchevskiy.pcs.dto.account;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import ru.borshchevskiy.pcs.enums.Role;

import java.util.Collection;
import java.util.Set;

@Data
public class AccountDto {

    private Long id;

    private String username;

    private String password;

    private Set<Role> roles;
}
