package com.example.clichepiggybank.service;

import com.example.clichepiggybank.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.UUID;

@Service
public class AccountStorageService {
    private final JdbcTemplate jdbcTemplate;

    public AccountStorageService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public synchronized HashMap<UUID, Account> loadAccounts() {
        HashMap<UUID, Account> accounts = new HashMap<>();
        jdbcTemplate.query(
                "SELECT id, owner_id, balance FROM accounts",
                (resultSet, rowNum) -> new Account(
                        resultSet.getObject("id", UUID.class),
                        resultSet.getObject("owner_id", UUID.class),
                        resultSet.getBigDecimal("balance").doubleValue()
                )
        ).forEach(account -> accounts.put(account.getId(), account));
        return accounts;
    }

    @Transactional
    public synchronized void saveAccounts(HashMap<UUID, Account> accountsMap) {
        jdbcTemplate.update("DELETE FROM accounts");
        accountsMap.values().forEach(account -> jdbcTemplate.update(
                "INSERT INTO accounts (id, owner_id, balance) VALUES (?, ?, ?)",
                account.getId(),
                account.getOwnerId(),
                BigDecimal.valueOf(account.getBalance())
        ));
    }
}
