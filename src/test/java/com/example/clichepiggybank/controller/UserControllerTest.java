package com.example.clichepiggybank.controller;

import com.example.clichepiggybank.model.Account;
import com.example.clichepiggybank.model.User;
import com.example.clichepiggybank.service.AccountStorageService;
import com.example.clichepiggybank.service.UserStorageService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserStorageService userStorageService;

    @MockitoBean
    private AccountStorageService accountStorageService;

    @Test
    void shouldReturnAllUsers() throws Exception {
        String guidString = "55c1dc52-fe0d-4447-a53a-5566f60c5113";
        UUID newGuid = UUID.fromString(guidString);
        String[] roles = {"user"};
        User user = new User(newGuid, "alice", roles);
        HashMap<UUID, User> map = new HashMap<>();
        map.put(newGuid, user);
        Mockito.when(userStorageService.loadUsers()).thenReturn(map);

        // Act & Assert
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..id").value("55c1dc52-fe0d-4447-a53a-5566f60c5113"))
                .andExpect(jsonPath("$..name").value("alice"))
                .andExpect(jsonPath("$..roles[0]").value("user"));
    }

    @Test
    void shouldCreatesUser() throws Exception {
        String adminGuidString = "c0a6d648-d769-4ebb-a0f0-7d44a0b0462e";
        UUID newAdminGuid = UUID.fromString(adminGuidString);
        String[] adminRoles = {"admin"};
        User admin = new User(newAdminGuid, "bob", adminRoles);
        String[] userRoles = {"user"};
        User user = new User(null, "alice", userRoles);
        HashMap<UUID, User> map = new HashMap<>();
        map.put(newAdminGuid, admin);
        Mockito.when(userStorageService.loadUsers()).thenReturn(map);

        mockMvc.perform(post("/api/users")
                        .param("inquirerid", "c0a6d648-d769-4ebb-a0f0-7d44a0b0462e")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(".id").exists())
                .andExpect(jsonPath(".name").value("alice"));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        String adminGuidString = "c0a6d648-d769-4ebb-a0f0-7d44a0b0462e";
        UUID newAdminGuid = UUID.fromString(adminGuidString);
        String[] adminRoles = {"admin"};
        User admin = new User(newAdminGuid, "bob", adminRoles);
        String[] userRoles = {"user"};
        String userGuidString = "990b0301-9262-4048-a4d6-4f7ad19eeb46";
        UUID newUserGuid = UUID.fromString(userGuidString);
        User user = new User(newUserGuid, "alice", userRoles);
        HashMap<UUID, User> userMap = new HashMap<>();
        userMap.put(newAdminGuid, admin);
        userMap.put(newUserGuid, user);
        String accountGuidString = "c46b72b7-0382-4a73-b92e-be7580029e56";
        UUID newAccountGuid = UUID.fromString(accountGuidString);
        Account account = new Account(newAccountGuid, newUserGuid, 0);
        HashMap<UUID, Account> accountMap = new HashMap<>();
        accountMap.put(newAccountGuid, account);
        Mockito.when(userStorageService.loadUsers()).thenReturn(userMap);
        Mockito.when(accountStorageService.loadAccounts()).thenReturn(accountMap);

        mockMvc.perform(delete("/api/users/{id}", "990b0301-9262-4048-a4d6-4f7ad19eeb46")
                        .param("inquirerid", "c0a6d648-d769-4ebb-a0f0-7d44a0b0462e")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(".id").value("990b0301-9262-4048-a4d6-4f7ad19eeb46"))
                .andExpect(jsonPath(".name").value("alice"));
    }

    @Test
    void shouldGetUser() throws Exception {
        String adminGuidString = "c0a6d648-d769-4ebb-a0f0-7d44a0b0462e";
        UUID newAdminGuid = UUID.fromString(adminGuidString);
        String[] adminRoles = {"admin"};
        User admin = new User(newAdminGuid, "bob", adminRoles);
        String[] userRoles = {"user"};
        String userGuidString = "990b0301-9262-4048-a4d6-4f7ad19eeb46";
        UUID newUserGuid = UUID.fromString(userGuidString);
        User user = new User(newUserGuid, "alice", userRoles);
        HashMap<UUID, User> userMap = new HashMap<>();
        userMap.put(newAdminGuid, admin);
        userMap.put(newUserGuid, user);
        Mockito.when(userStorageService.loadUsers()).thenReturn(userMap);

        mockMvc.perform(get("/api/users/{id}", "990b0301-9262-4048-a4d6-4f7ad19eeb46")
                        .param("inquirerid", "c0a6d648-d769-4ebb-a0f0-7d44a0b0462e")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(".id").value("990b0301-9262-4048-a4d6-4f7ad19eeb46"))
                .andExpect(jsonPath(".name").value("alice"));
    }
}
