package com.example.clichepiggybank.controller;

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
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
        System.out.println(map);
        Mockito.when(userStorageService.loadUsers()).thenReturn(map);

        // Act & Assert
        mockMvc.perform(get("/api/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..id").value("55c1dc52-fe0d-4447-a53a-5566f60c5113"))
                .andExpect(jsonPath("$..name").value("alice"))
                .andExpect(jsonPath("$..roles[0]").value("user"));
    }

}
