package com.example.clichepiggybank.service;

import com.example.clichepiggybank.model.Sanction;
import com.example.clichepiggybank.model.SanctionAmount;
import com.example.clichepiggybank.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SanctionStorageServiceTest {
    @Autowired
    private SanctionStorageService storageService;

    @BeforeEach
    void clearDatabase() {
        storageService.saveSanctions(new HashMap<>());
    }

    @Test
    void shouldReturnEmptyMapWhenDatabaseHasNoSanctions() {
        assertThat(storageService.loadSanctions()).isEmpty();
    }

    @Test
    void shouldSaveAndLoadSanctionDetailsAndLikes() {
        UUID id = UUID.randomUUID();
        UUID reporterId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        UUID likerId = UUID.randomUUID();
        Date createdAt = new Date(1_800_000_000_000L);
        Sanction sanction = new Sanction(
                id,
                new User(reporterId, "Reporter", new String[]{"user"}),
                new User(receiverId, "Receiver", new String[]{"user"}),
                SanctionAmount.HIGH,
                "Sample reason",
                createdAt,
                Set.of(likerId),
                1
        );
        HashMap<UUID, Sanction> sanctions = new HashMap<>();
        sanctions.put(id, sanction);

        storageService.saveSanctions(sanctions);

        Sanction loaded = storageService.loadSanctions().get(id);
        assertThat(loaded.getReporter().getId()).isEqualTo(reporterId);
        assertThat(loaded.getReporter().getRoles()).containsExactly("user");
        assertThat(loaded.getReceiver().getName()).isEqualTo("Receiver");
        assertThat(loaded.getAmount()).isEqualTo(SanctionAmount.HIGH);
        assertThat(loaded.getReason()).isEqualTo("Sample reason");
        assertThat(loaded.getDatetime()).isEqualTo(createdAt);
        assertThat(loaded.getLikedBy()).containsExactly(likerId);
        assertThat(loaded.getLikes()).isEqualTo(1);
    }
}
