package com.example.clichepiggybank.controller;

import com.example.clichepiggybank.controller.exceptions.AccountNotEmptyException;
import com.example.clichepiggybank.controller.exceptions.ForbiddenException;
import com.example.clichepiggybank.controller.exceptions.UserNotFoundException;
import com.example.clichepiggybank.model.Account;
import com.example.clichepiggybank.model.User;
import com.example.clichepiggybank.service.AccountStorageService;
import com.example.clichepiggybank.service.UserStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserStorageService userStorageService;
    private final AccountStorageService accountStorageService;

    public UserController(
            UserStorageService userStorageService,
            AccountStorageService accountStorageService
    ) {
        this.userStorageService = userStorageService;
        this.accountStorageService = accountStorageService;
    }

    @GetMapping
    public ResponseEntity<HashMap<String, User>> getAllUsers() {
        return ResponseEntity.ok(userStorageService.loadUsers());
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User newUser, @RequestParam("inquirerid") String inquirerId) {
        HashMap<String, User> current = userStorageService.loadUsers();
        if (!isAdmin(current, inquirerId)) {
            throw new ForbiddenException(inquirerId);
        }
        String guid = UUID.randomUUID().toString();
        while(current.containsKey(guid)) {
            guid = UUID.randomUUID().toString();
        }
        newUser.setId(guid);
        current.put(guid, newUser);
        userStorageService.saveUsers(current);
        return ResponseEntity.ok(newUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable String id, @RequestParam("inquirerid") String inquirerId) {
        HashMap<String, User> currentUsers = userStorageService.loadUsers();
        HashMap<String, Account> currentAccounts = accountStorageService.loadAccounts();

        if (!isAdmin(currentUsers, inquirerId)) {
            throw new ForbiddenException(inquirerId);
        }
        if (!currentUsers.containsKey(id)) {
            throw new UserNotFoundException(id);
        }
        Account toBeVerified = currentAccounts.values().stream().filter(account -> account.getOwnerId().equals(id)).findFirst().orElse(null);
        if (toBeVerified != null) {
            if (toBeVerified.getBalance() > 0) {
                throw new AccountNotEmptyException(toBeVerified.getId());
            }
        }
        User toBeDeleted = currentUsers.get(id);
        currentUsers.remove(id);
        userStorageService.saveUsers(currentUsers);
        return ResponseEntity.ok(toBeDeleted);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable String id) {
        HashMap<String, User> users = userStorageService.loadUsers();
        if (!users.containsKey(id)) {
            throw new UserNotFoundException(id);
        }
        return ResponseEntity.ok(users.get(id));
    }

    public static boolean isAdmin(HashMap<String, User> current, String inquirerId) {
        User inquirer;
        try {
            inquirer = current
                    .values()
                    .stream()
                    .filter(user -> user.getId().equals(inquirerId))
                    .findFirst()
                    .orElseThrow();
        } catch (NoSuchElementException e) {
            return false;
        }
        return Arrays.asList(inquirer.getRoles()).contains("admin");
    }
}
