package com.example.service;

import com.example.entity.Account;
import com.example.repository.AccountRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final AccountRepository repo;

    public AccountService(AccountRepository repo) {
        this.repo = repo;
    }

    // Registering new accounts and returning save accounts or throws for controller to map status
    public Account register(Account incoming) {
        if (incoming == null
                || incoming.getUsername() == null
                || incoming.getUsername().isBlank()
                || incoming.getPassword() == null
                || incoming.getPassword().length() < 4) {
            throw new IllegalArgumentException("Bad registration payload");
        }
        if (repo.existsByUsername(incoming.getUsername())) {
            throw new DuplicateUsernameException();
        }
        return repo.save(incoming);
    }

    // Login by credentials login/username and return null if not found
    public Account login(String username, String password) {
        return repo.findByUsernameAndPassword(username, password).orElse(null);
    }

    // Marker for 409 mapping
    public static class DuplicateUsernameException extends RuntimeException {}
}
