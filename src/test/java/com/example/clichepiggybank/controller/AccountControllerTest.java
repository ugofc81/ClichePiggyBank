package com.example.clichepiggybank.controller;

import com.example.clichepiggybank.model.Account;
import com.example.clichepiggybank.service.AccountStorageService;
import com.example.clichepiggybank.service.UserStorageService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserStorageService userStorageService;

    @MockitoBean
    private AccountStorageService accountStorageService;

    public static Account produceAccount(final String accountGuidString, final String ownerGuidString, final double balance) {
        UUID accountGuid = UUID.fromString(accountGuidString);
        UUID ownerGuid = UUID.fromString(ownerGuidString);
        return new Account(accountGuid, ownerGuid, balance);
    }

    @Test
    void shouldReturnAllAccounts() throws Exception {
        Account account = produceAccount("c46b72b7-0382-4a73-b92e-be7580029e56", "55c1dc52-fe0d-4447-a53a-5566f60c5113", 5);
        HashMap<UUID, Account> map = new HashMap<>();
        map.put(account.getId(), account);
        Mockito.when(accountStorageService.loadAccounts()).thenReturn(map);

        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..id").value(account.getId().toString()))
                .andExpect(jsonPath("$..ownerId").value(account.getOwnerId().toString()))
                .andExpect(jsonPath("$..balance").value(account.getBalance()));
    }
}