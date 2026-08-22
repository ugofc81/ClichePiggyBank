package com.example.clichepiggybank.controller;

import com.example.clichepiggybank.controller.exceptions.AccountNotFoundException;
import com.example.clichepiggybank.controller.exceptions.ForbiddenException;
import com.example.clichepiggybank.model.Account;
import com.example.clichepiggybank.model.User;
import com.example.clichepiggybank.service.AccountStorageService;
import com.example.clichepiggybank.service.UserStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountStorageService accountStorageService;
    private final UserStorageService userStorageService;

    public AccountController(
            AccountStorageService accountStorageService,
            UserStorageService userStorageService
    ) {
        this.accountStorageService = accountStorageService;
        this.userStorageService = userStorageService;
    }

    @GetMapping
    public ResponseEntity<HashMap<UUID, Account>> getAllAccounts() {
        return ResponseEntity.ok(accountStorageService.loadAccounts());
    }

    @PostMapping
    public ResponseEntity<Account> createAccount(User user) {
        HashMap<UUID, Account> current = accountStorageService.loadAccounts();
        UUID guid = UUID.randomUUID();
        while(current.containsKey(guid)) {
            guid = UUID.randomUUID();
        }
        Account newAccount = new Account(guid, user.getId(), 0);
        current.put(guid, newAccount);
        accountStorageService.saveAccounts(current);
        return ResponseEntity.ok(newAccount);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccount(@PathVariable UUID id) {
        HashMap<UUID, Account> accounts = accountStorageService.loadAccounts();
        if(!accounts.containsKey(id)) {
            throw new AccountNotFoundException(id);
        }
        return ResponseEntity.ok(accounts.get(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Account> updateAccount(@PathVariable UUID id, @RequestBody Account updatedAccount) {
        HashMap<UUID, Account> accounts = accountStorageService.loadAccounts();
        if(!accounts.containsKey(id)) {
            throw new AccountNotFoundException(id);
        }
        accounts.put(id, updatedAccount);
        accountStorageService.saveAccounts(accounts);
        return ResponseEntity.ok(updatedAccount);
    }

    @PutMapping("/{id}/reset")
    public ResponseEntity<Account> resetAccount(@PathVariable UUID id, @RequestParam("inquirerid") UUID inquirerId) {
        HashMap<UUID, Account> accounts = accountStorageService.loadAccounts();
        HashMap<UUID, User> users = userStorageService.loadUsers();
        if (!UserController.isAdmin(users, inquirerId)) {
            throw new ForbiddenException(inquirerId);
        }
        if(!accounts.containsKey(id)) {
            throw new AccountNotFoundException(id);
        }
        Account current = accounts.get(id);
        current.setBalance(0);
        accounts.put(id, current);
        accountStorageService.saveAccounts(accounts);
        return ResponseEntity.ok(current);
    }
}
