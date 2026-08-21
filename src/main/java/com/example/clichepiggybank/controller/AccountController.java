package com.example.clichepiggybank.controller;

import com.example.clichepiggybank.model.Account;
import com.example.clichepiggybank.model.User;
import com.example.clichepiggybank.service.AccountStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountStorageService accountStorageService;

    public AccountController(AccountStorageService accountStorageService) {
        this.accountStorageService = accountStorageService;
    }

    @GetMapping
    public HashMap<String, Account> getAllAccounts() {
        return accountStorageService.loadAccounts();
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
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(accounts.get(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Account> updateAccount(@PathVariable String id, @RequestBody Account updatedAccount) {
        HashMap<String, Account> accounts = accountStorageService.loadAccounts();
        if(!accounts.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        accounts.put(id, updatedAccount);
        accountStorageService.saveAccounts(accounts);
        return ResponseEntity.ok(updatedAccount);
    }

    @PutMapping("/reset/{id}")
    public ResponseEntity<Account> resetAccount(@PathVariable String id) {
        HashMap<String, Account> accounts = accountStorageService.loadAccounts();
        if(!accounts.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        Account current = accounts.get(id);
        current.setBalance(0);
        accounts.put(id, current);
        accountStorageService.saveAccounts(accounts);
        return ResponseEntity.ok(current);
    }
}
