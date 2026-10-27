package com.example.clichepiggybank.service;

import com.example.clichepiggybank.model.Sanction;
import com.example.clichepiggybank.model.SanctionAmount;
import com.example.clichepiggybank.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class SanctionStorageService {
    private final JdbcTemplate jdbcTemplate;

    public SanctionStorageService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public synchronized HashMap<UUID, Sanction> loadSanctions() {
        HashMap<UUID, Sanction> sanctions = new HashMap<>();
        jdbcTemplate.query(
                "SELECT id, reporter_id, reporter_name, receiver_id, receiver_name, amount, reason, created_at, likes FROM sanctions",
                (resultSet, rowNum) -> {
                    Timestamp createdAt = resultSet.getTimestamp("created_at");
                    return new Sanction(
                            resultSet.getObject("id", UUID.class),
                            new User(
                                    resultSet.getObject("reporter_id", UUID.class),
                                    resultSet.getString("reporter_name"),
                                    new String[0]
                            ),
                            new User(
                                    resultSet.getObject("receiver_id", UUID.class),
                                    resultSet.getString("receiver_name"),
                                    new String[0]
                            ),
                            SanctionAmount.valueOf(resultSet.getString("amount")),
                            resultSet.getString("reason"),
                            createdAt == null ? null : new Date(createdAt.getTime()),
                            new HashSet<>(),
                            resultSet.getInt("likes")
                    );
                }
        ).forEach(sanction -> sanctions.put(sanction.getId(), sanction));

        jdbcTemplate.query(
                "SELECT sanction_id, side, role FROM sanction_user_roles ORDER BY sanction_id, side, role_order",
                (ResultSetExtractor<Void>) resultSet -> {
                    while (resultSet.next()) {
                        Sanction sanction = sanctions.get(resultSet.getObject("sanction_id", UUID.class));
                        if (sanction != null) {
                            User user = "reporter".equals(resultSet.getString("side"))
                                    ? sanction.getReporter()
                                    : sanction.getReceiver();
                            List<String> roles = new ArrayList<>(List.of(user.getRoles()));
                            roles.add(resultSet.getString("role"));
                            user.setRoles(roles.toArray(String[]::new));
                        }
                    }
                    return null;
                }
        );
        jdbcTemplate.query(
                "SELECT sanction_id, user_id FROM sanction_likes",
                (ResultSetExtractor<Void>) resultSet -> {
                    while (resultSet.next()) {
                        Sanction sanction = sanctions.get(resultSet.getObject("sanction_id", UUID.class));
                        if (sanction != null) {
                            sanction.getLikedBy().add(resultSet.getObject("user_id", UUID.class));
                        }
                    }
                    return null;
                }
        );
        return sanctions;
    }

    @Transactional
    public synchronized void saveSanctions(HashMap<UUID, Sanction> sanctionsMap) {
        jdbcTemplate.update("DELETE FROM sanction_user_roles");
        jdbcTemplate.update("DELETE FROM sanction_likes");
        jdbcTemplate.update("DELETE FROM sanctions");

        sanctionsMap.values().forEach(sanction -> {
            Date datetime = sanction.getDatetime();
            jdbcTemplate.update(
                    "INSERT INTO sanctions (id, reporter_id, reporter_name, receiver_id, receiver_name, amount, reason, created_at, likes) "
                            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    sanction.getId(),
                    sanction.getReporter().getId(),
                    sanction.getReporter().getName(),
                    sanction.getReceiver().getId(),
                    sanction.getReceiver().getName(),
                    sanction.getAmount().name(),
                    sanction.getReason(),
                    datetime == null ? null : new Timestamp(datetime.getTime()),
                    sanction.getLikes() == null ? 0 : sanction.getLikes()
            );
            saveRoles(sanction.getId(), "reporter", sanction.getReporter());
            saveRoles(sanction.getId(), "receiver", sanction.getReceiver());
            Set<UUID> likedBy = sanction.getLikedBy() == null
                    ? Collections.emptySet()
                    : sanction.getLikedBy();
            likedBy.forEach(userId -> jdbcTemplate.update(
                    "INSERT INTO sanction_likes (sanction_id, user_id) VALUES (?, ?)",
                    sanction.getId(), userId
            ));
        });
    }

    private void saveRoles(UUID sanctionId, String side, User user) {
        String[] roles = user.getRoles();
        if (roles == null) {
            return;
        }
        for (int index = 0; index < roles.length; index++) {
            jdbcTemplate.update(
                    "INSERT INTO sanction_user_roles (sanction_id, side, role_order, role) VALUES (?, ?, ?, ?)",
                    sanctionId, side, index, roles[index]
            );
        }
    }
}
