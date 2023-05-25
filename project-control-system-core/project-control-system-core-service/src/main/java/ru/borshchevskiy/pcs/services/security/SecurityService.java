package ru.borshchevskiy.pcs.services.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.borshchevskiy.pcs.entities.employee.Employee;
import ru.borshchevskiy.pcs.enums.EmployeeStatus;
import ru.borshchevskiy.pcs.exceptions.DeletedItemModificationException;
import ru.borshchevskiy.pcs.repository.employee.EmployeeRepository;

@RequiredArgsConstructor
@Service
public class SecurityService implements UserDetailsService {

    private final EmployeeRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = repository.findByAccount(username)
                .map(e -> {
                    if (e.getStatus() == EmployeeStatus.DELETED) {
                        throw new DeletedItemModificationException("Employee deleted!");
                    }
                    return e;
                })
                .orElseThrow(() -> new UsernameNotFoundException("User with username: " + username + " not found"));
        return new User(employee.getAccount(), employee.getPassword(), employee.getRoles());
    }
}
