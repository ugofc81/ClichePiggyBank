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
    public ResponseEntity<HashMap<String, Account>> getAllAccounts() {
        return ResponseEntity.ok(accountStorageService.loadAccounts());
    }

    @PostMapping
    public ResponseEntity<Account> createAccount(User user) {
        HashMap<String, Account> current = accountStorageService.loadAccounts();
        String guid = UUID.randomUUID().toString();
        while(current.containsKey(guid)) {
            guid = UUID.randomUUID().toString();
        }
        Account newAccount = new Account(guid, user.getId(), 0);
        current.put(guid, newAccount);
        accountStorageService.saveAccounts(current);
        return ResponseEntity.ok(newAccount);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccount(@PathVariable String id) {
        HashMap<String, Account> accounts = accountStorageService.loadAccounts();
        if(!accounts.containsKey(id)) {
            throw new AccountNotFoundException(id);
        }
        return ResponseEntity.ok(accounts.get(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Account> updateAccount(@PathVariable String id, @RequestBody Account updatedAccount) {
        HashMap<String, Account> accounts = accountStorageService.loadAccounts();
        if(!accounts.containsKey(id)) {
            throw new AccountNotFoundException(id);
        }
        accounts.put(id, updatedAccount);
        accountStorageService.saveAccounts(accounts);
        return ResponseEntity.ok(updatedAccount);
    }

    @PutMapping("/reset/{id}")
    public ResponseEntity<Account> resetAccount(@PathVariable String id, @RequestParam("inquirerid") String inquirerId) {
        HashMap<String, Account> accounts = accountStorageService.loadAccounts();
        HashMap<String, User> users = userStorageService.loadUsers();
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
