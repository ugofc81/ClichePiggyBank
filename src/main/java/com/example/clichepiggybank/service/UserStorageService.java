package com.example.clichepiggybank.service;

import com.example.clichepiggybank.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class UserStorageService {
    private final JdbcTemplate jdbcTemplate;

    public UserStorageService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public synchronized HashMap<UUID, User> loadUsers() {
        HashMap<UUID, User> users = new HashMap<>();
        jdbcTemplate.query("SELECT id, name FROM users", (resultSet, rowNum) -> new User(
                resultSet.getObject("id", UUID.class),
                resultSet.getString("name"),
                new String[0]
        )).forEach(user -> users.put(user.getId(), user));

        jdbcTemplate.query("SELECT user_id, role FROM user_roles ORDER BY user_id, role_order",
                (ResultSetExtractor<Void>) resultSet -> {
            while (resultSet.next()) {
                UUID userId = resultSet.getObject("user_id", UUID.class);
                User user = users.get(userId);
                if (user != null) {
                    List<String> roles = new ArrayList<>(List.of(user.getRoles()));
                    roles.add(resultSet.getString("role"));
                    user.setRoles(roles.toArray(String[]::new));
                }
            }
            return null;
        });
        return users;
    }

    @Transactional
    public synchronized void saveUsers(HashMap<UUID, User> usersMap) {
        jdbcTemplate.update("DELETE FROM user_roles");
        jdbcTemplate.update("DELETE FROM users");

        usersMap.values().forEach(user -> {
            jdbcTemplate.update("INSERT INTO users (id, name) VALUES (?, ?)", user.getId(), user.getName());
            String[] roles = user.getRoles();
            if (roles != null) {
                for (int index = 0; index < roles.length; index++) {
                    jdbcTemplate.update(
                            "INSERT INTO user_roles (user_id, role_order, role) VALUES (?, ?, ?)",
                            user.getId(), index, roles[index]
                    );
                }
            }
        });
    }
}
