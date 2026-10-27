package com.example.clichepiggybank.service;

import com.example.clichepiggybank.model.Account;
import com.example.clichepiggybank.model.Sanction;
import com.example.clichepiggybank.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

@Component
public class LegacyJsonDataImporter implements ApplicationRunner {
    private static final String MIGRATION_ID = "legacy-json-v1";

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    private final UserStorageService userStorageService;
    private final AccountStorageService accountStorageService;
    private final SanctionStorageService sanctionStorageService;
    private final String userFile;
    private final String accountFile;
    private final String sanctionFile;

    public LegacyJsonDataImporter(
            JdbcTemplate jdbcTemplate,
            ObjectMapper objectMapper,
            UserStorageService userStorageService,
            AccountStorageService accountStorageService,
            SanctionStorageService sanctionStorageService,
            @Value("${legacy-data.user-file}") String userFile,
            @Value("${legacy-data.account-file}") String accountFile,
            @Value("${legacy-data.sanction-file}") String sanctionFile
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
        this.userStorageService = userStorageService;
        this.accountStorageService = accountStorageService;
        this.sanctionStorageService = sanctionStorageService;
        this.userFile = userFile;
        this.accountFile = accountFile;
        this.sanctionFile = sanctionFile;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Integer imported = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM legacy_data_imports WHERE migration_id = ?",
                Integer.class,
                MIGRATION_ID
        );
        if (imported != null && imported > 0) {
            return;
        }

        if (hasExistingDatabaseData()) {
            markComplete();
            return;
        }

        try {
            userStorageService.saveUsers(readMap(userFile, new TypeReference<>() {}));
            accountStorageService.saveAccounts(readMap(accountFile, new TypeReference<>() {}));
            sanctionStorageService.saveSanctions(readMap(sanctionFile, new TypeReference<>() {}));
            markComplete();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to import legacy JSON data into the database", exception);
        }
    }

    private boolean hasExistingDatabaseData() {
        return countRows("users") > 0
                || countRows("accounts") > 0
                || countRows("sanctions") > 0;
    }

    private long countRows(String table) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + table, Long.class);
    }

    private void markComplete() {
        jdbcTemplate.update("INSERT INTO legacy_data_imports (migration_id) VALUES (?)", MIGRATION_ID);
    }

    private <T> HashMap<UUID, T> readMap(String path, TypeReference<HashMap<UUID, T>> type) throws IOException {
        File file = new File(path);
        if (!file.isFile()) {
            return new HashMap<>();
        }
        return objectMapper.readValue(file, type);
    }
}
