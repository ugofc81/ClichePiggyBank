package com.example.clichepiggybank.model;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SanctionTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldGetAndSetFieldsCorrectly() {
        Sanction sanction = new Sanction(null, null, null, null, null, null, null, null);
        UUID newSanctionGuid = UUID.fromString("55c1dc52-fe0d-4447-a53a-5566f60c5113");
        UUID newReporterGuid = UUID.fromString("be4523cf-6cba-4747-8989-ce8e21470e34");
        UUID newReceiverGuid = UUID.fromString("f6774142-8b20-45fd-bd62-c1dab2a678f6");
        String[] roles = {"user"};
        User newReporter = new User(newReporterGuid, "alice", roles);
        User newReceiver = new User(newReceiverGuid, "bob", roles);
        Date datetime = new Date();
        Set<UUID> emptySet = Collections.emptySet();
        sanction.setId(newSanctionGuid);
        sanction.setReporter(newReporter);
        sanction.setReceiver(newReceiver);
        sanction.setAmount(SanctionAmount.HIGH);
        sanction.setReason("some reason");
        sanction.setDatetime(datetime);
        sanction.setLikedBy(emptySet);
        sanction.setLikes(0);

        assertThat(sanction.getId()).isEqualTo(newSanctionGuid);
        assertThat(sanction.getReporter().getId()).isEqualTo(newReporterGuid);
        assertThat(sanction.getReceiver().getId()).isEqualTo(newReceiverGuid);
        assertThat(sanction.getAmount()).isEqualTo(SanctionAmount.HIGH);
        assertThat(sanction.getLikedBy()).isEqualTo(emptySet);
        assertThat(sanction.getLikes()).isEqualTo(0);
    }

    @Test
    void shouldSerializeToJsonSuccessfully() throws Exception {
        UUID newSanctionGuid = UUID.fromString("55c1dc52-fe0d-4447-a53a-5566f60c5113");
        UUID newReporterGuid = UUID.fromString("be4523cf-6cba-4747-8989-ce8e21470e34");
        UUID newReceiverGuid = UUID.fromString("f6774142-8b20-45fd-bd62-c1dab2a678f6");
        String[] roles = {"user"};
        User newReporter = new User(newReporterGuid, "alice", roles);
        User newReceiver = new User(newReceiverGuid, "bob", roles);
        Date datetime = new Date();
        Set<UUID> emptySet = Collections.emptySet();
        Sanction sanction = new Sanction(
                newSanctionGuid,
                newReporter,
                newReceiver,
                SanctionAmount.HIGH,
                "new reason",
                datetime,
                emptySet,
                0);

        String jsonResult = objectMapper.writeValueAsString(sanction);

        assertThat(jsonResult)
                .contains("\"id\":\"55c1dc52-fe0d-4447-a53a-5566f60c5113\"")
                .contains("\"id\":\"be4523cf-6cba-4747-8989-ce8e21470e34\"")
                .contains("\"id\":\"f6774142-8b20-45fd-bd62-c1dab2a678f6\"")
                .contains("\"reason\":\"new reason\"");
    }

    @Test
    void shouldDeserializeFromJsonSuccessfully() throws Exception {
        // Arrange - A JSON string exactly like what Postman sends
        String inputJson = "{\n" +
                "    \"id\": \"7b485c91-95af-4153-b51a-6b4310a368e0\",\n" +
                "    \"reporter\": {\n" +
                "      \"id\": \"27ee6c9a-ce75-4515-a4a8-27e755efe0e2\",\n" +
                "      \"name\": \"Pippo\",\n" +
                "      \"roles\": [\n" +
                "        \"user\"\n" +
                "      ]\n" +
                "    },\n" +
                "    \"receiver\": {\n" +
                "      \"id\": \"fba592ba-36c3-4479-ba38-bcaeceff9ff0\",\n" +
                "      \"name\": \"Sebastian\",\n" +
                "      \"roles\": null\n" +
                "    },\n" +
                "    \"amount\": \"STANDARD\",\n" +
                "    \"reason\": \"abce\",\n" +
                "    \"datetime\": \"2026-10-01T08:22:15.032Z\",\n" +
                "    \"likedBy\": [],\n" +
                "    \"likes\": 0\n" +
                "  }";

        // Act - Turn the JSON string back into a Java Sanction object
        Sanction sanctionResult = objectMapper.readValue(inputJson, Sanction.class);
        String[] roles = {"sanction"};

        // Assert
        assertThat(sanctionResult).isNotNull();
        assertThat(sanctionResult.getId().toString()).isEqualTo("7b485c91-95af-4153-b51a-6b4310a368e0");
        assertThat(sanctionResult.getReporter().getId().toString()).isEqualTo("27ee6c9a-ce75-4515-a4a8-27e755efe0e2");
        assertThat(sanctionResult.getReceiver().getId().toString()).isEqualTo("fba592ba-36c3-4479-ba38-bcaeceff9ff0");
        assertThat(sanctionResult.getAmount()).isEqualTo(SanctionAmount.STANDARD);
        assertThat(sanctionResult.getReason()).isEqualTo("abce");
    }
}