package com.example.clichepiggybank.controller;

import com.example.clichepiggybank.controller.exceptions.ForbiddenException;
import com.example.clichepiggybank.controller.exceptions.UserNotFoundException;
import com.example.clichepiggybank.model.User;
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

    public UserController(UserStorageService userStorageService) {
        this.userStorageService = userStorageService;
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
        HashMap<String, User> current = userStorageService.loadUsers();
        if (!isAdmin(current, inquirerId)) {
            throw new ForbiddenException(inquirerId);
        }
        if (!current.containsKey(id)) {
            throw new UserNotFoundException(id);
        }
        User toBeDeleted = current.get(id);
        current.remove(id);
        userStorageService.saveUsers(current);
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

    private boolean isAdmin(HashMap<String, User> current, String inquirerId) {
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
