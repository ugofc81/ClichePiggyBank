package com.example.clichepiggybank.model;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldGetAndSetFieldsCorrectly() {
        User user = new User(null, null, null);
        UUID newGuid = UUID.fromString("55c1dc52-fe0d-4447-a53a-5566f60c5113");
        String[] roles = {"user"};
        user.setId(newGuid);
        user.setName("john_doe");
        user.setRoles(roles);

        assertThat(user.getId()).isEqualTo(newGuid);
        assertThat(user.getName()).isEqualTo("john_doe");
        assertThat(user.getRoles()).isEqualTo(roles);
    }

    @Test
    void shouldSerializeToJsonSuccessfully() throws Exception {
        UUID newGuid = UUID.fromString("55c1dc52-fe0d-4447-a53a-5566f60c5113");
        String[] roles = {"user"};
        User user = new User(newGuid, "alice", roles);

        String jsonResult = objectMapper.writeValueAsString(user);

        assertThat(jsonResult)
                .contains("\"id\":\"55c1dc52-fe0d-4447-a53a-5566f60c5113\"")
                .contains("\"name\":\"alice\"")
                .contains("\"roles\":[\"user\"]");
    }

    @Test
    void shouldDeserializeFromJsonSuccessfully() throws Exception {
        // Arrange - A JSON string exactly like what Postman sends
        String inputJson = "{\"id\":\"55c1dc52-fe0d-4447-a53a-5566f60c5113\",\"name\":\"bob\",\"roles\":[\"user\"]}";

        // Act - Turn the JSON string back into a Java User object
        User userResult = objectMapper.readValue(inputJson, User.class);
        String[] roles = {"user"};

        // Assert
        assertThat(userResult).isNotNull();
        assertThat(userResult.getId().toString()).isEqualTo("55c1dc52-fe0d-4447-a53a-5566f60c5113");
        assertThat(userResult.getName()).isEqualTo("bob");
        assertThat(userResult.getRoles()).isEqualTo(roles);
    }
}