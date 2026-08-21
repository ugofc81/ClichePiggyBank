package com.example.clichepiggybank.controller;

import com.example.clichepiggybank.model.User;
import com.example.clichepiggybank.service.UserStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserStorageService userStorageService;

    public UserController(UserStorageService userStorageService) {
        this.userStorageService = userStorageService;
    }

    @GetMapping
    public HashMap<String, User> getAllUsers() {
        return userStorageService.loadUsers();
    }

    @PostMapping
    public User createUser(@RequestBody User newUser) {
        HashMap<String, User> current = userStorageService.loadUsers();
        String guid = UUID.randomUUID().toString();
        while(current.containsKey(guid)) {
            guid = UUID.randomUUID().toString();
        }
        newUser.setId(guid);
        current.put(guid, newUser);
        userStorageService.saveUsers(current);
        return newUser;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        boolean isDeleted = userStorageService.deleteUserById(id);
        if (isDeleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable String id) {
        HashMap<String, User> users = userStorageService.loadUsers();
        if (!users.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(users.get(id));
    }
}
