package com.example.clichepiggybank.service;

import com.example.clichepiggybank.model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AccountStorageServiceTest {
    @Autowired
    private AccountStorageService storageService;

    @BeforeEach
    void clearDatabase() {
        storageService.saveAccounts(new HashMap<>());
    }

    @Test
    void shouldReturnEmptyMapWhenDatabaseHasNoAccounts() {
        assertThat(storageService.loadAccounts()).isEmpty();
    }

    @Test
    void shouldSaveAndLoadAccountDetails() {
        UUID id = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        HashMap<UUID, Account> accounts = new HashMap<>();
        accounts.put(id, new Account(id, ownerId, 5.50));

        storageService.saveAccounts(accounts);

        assertThat(storageService.loadAccounts())
                .containsKey(id)
                .extractingByKey(id)
                .satisfies(account -> {
                    assertThat(account.getOwnerId()).isEqualTo(ownerId);
                    assertThat(account.getBalance()).isEqualTo(5.50);
                });
    }
}
