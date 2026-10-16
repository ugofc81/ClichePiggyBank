package com.example.clichepiggybank.controller;

import com.example.clichepiggybank.model.Sanction;
import com.example.clichepiggybank.model.User;
import com.example.clichepiggybank.service.AccountStorageService;
import com.example.clichepiggybank.service.SanctionStorageService;
import com.example.clichepiggybank.service.UserStorageService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest(SanctionController.class)
class SanctionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SanctionStorageService sanctionStorageService;

    @MockitoBean
    private UserStorageService userStorageService;

    @MockitoBean
    private AccountStorageService accountStorageService;

    // Inner class or helper mimicking a custom amount record/class if used in your model
    public static record SanctionAmount(double amount) {}

    @Test
    void shouldCreateSanctionAndIndirectlyCreateAndChargeAccount() throws Exception {
        String inquirerId = "990b0301-9262-4048-a4d6-4f7ad19eeb46";
        String receiverId = "c0a6d648-d769-4ebb-a0f0-7d44a0b0462e";
        UUID sanctionId = UUID.randomUUID();

        User reporter = UserControllerTest.produceUser(inquirerId, "alice", "user");
        User receiver = UserControllerTest.produceUser(receiverId, "bob", "user");

        HashMap<UUID, User> mockUserMap = new HashMap<>();
        mockUserMap.put(UUID.fromString(inquirerId), reporter);
        mockUserMap.put(UUID.fromString(receiverId), receiver);

        // 3. Stub the storage layers
        Mockito.when(userStorageService.loadUsers()).thenReturn(mockUserMap);
        Mockito.when(accountStorageService.loadAccounts()).thenReturn(new HashMap<>()); // Triggers account creation branch!
        Mockito.when(sanctionStorageService.loadSanctions()).thenReturn(new HashMap<>());

        // 4. Formulate the input body Sanction
        Sanction incomingSanction = new Sanction(
                sanctionId,
                reporter,
                receiver,
                com.example.clichepiggybank.model.SanctionAmount.HIGH,
                "run you fools",
                new Date(),
                Collections.emptySet(),
                0
                );

        // Act & Assert
        mockMvc.perform(post("/api/sanctions")
                        .param("inquirerid", inquirerId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomingSanction)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.reporter.name").value("alice"))
                .andExpect(jsonPath("$.receiver.name").value("bob"));

        // 5. Verification: Check that AccountController logic successfully instructed account data to be saved!
        Mockito.verify(accountStorageService, Mockito.atLeastOnce()).saveAccounts(any(HashMap.class));
        Mockito.verify(sanctionStorageService, Mockito.times(1)).saveSanctions(any(HashMap.class));
    }
}
