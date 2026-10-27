package com.example.clichepiggybank.service;

import com.example.clichepiggybank.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserStorageServiceTest {
    @Autowired
    private UserStorageService storageService;

    @BeforeEach
    void clearDatabase() {
        storageService.saveUsers(new HashMap<>());
    }

    @Test
    void shouldReturnEmptyMapWhenDatabaseHasNoUsers() {
        assertThat(storageService.loadUsers()).isEmpty();
    }

    @Test
    void shouldSaveAndLoadUsersWithTheirRoles() {
        UUID id = UUID.randomUUID();
        HashMap<UUID, User> users = new HashMap<>();
        users.put(id, new User(id, "test_user", new String[]{"user", "admin"}));

        storageService.saveUsers(users);

        assertThat(storageService.loadUsers())
                .containsKey(id)
                .extractingByKey(id)
                .satisfies(user -> {
                    assertThat(user.getName()).isEqualTo("test_user");
                    assertThat(user.getRoles()).containsExactly("user", "admin");
                });
    }
}
