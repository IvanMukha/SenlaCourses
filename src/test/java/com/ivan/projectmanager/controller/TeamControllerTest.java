package com.ivan.projectmanager.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestControllerConfiguration.class)
@WebAppConfiguration
public class TeamControllerTest {
    private MockMvc mockMvc;

    @BeforeEach
    void setUp(WebApplicationContext wac) {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity()).build();
    }

    @WithMockUser(username = "username", roles = {"USER"})
    @Test
    @Sql("classpath:data/teamrepositorytests/insert-teams.sql")
    void testGetAllTeams() throws Exception {
        mockMvc.perform(get("/teams")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].name").value("name"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @WithMockUser(username = "username", roles = {"USER"})
    @Test
    @Sql("classpath:data/teamrepositorytests/insert-teams.sql")
    void testGetTeamById() throws Exception {
        mockMvc.perform(get("/teams/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("name"));
    }

    @WithMockUser(username = "username", roles = {"ADMIN"})
    @Test
    void testSaveTeam() throws Exception {
        String requestBody = "{\"name\": \"saved name\"}";
        mockMvc.perform(post("/teams")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("saved name"));

    }

    @WithMockUser(username = "username", roles = {"ADMIN"})
    @Test
    @Sql("classpath:data/teamrepositorytests/insert-teams.sql")
    void testUpdateTeam() throws Exception {
        String requestBody = "{\"name\": \"updated name\"}";
        mockMvc.perform(put("/teams/1")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("updated name"));
    }

    @WithMockUser(username = "username", roles = {"ADMIN"})
    @Test
    @Sql("classpath:data/teamrepositorytests/insert-teams.sql")
    void testDeleteTeam() throws Exception {
        mockMvc.perform(delete("/teams/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "username", roles = {"USER"})
    @Test
    public void testAccessDenied() throws Exception {
        String requestBody = "{\"name\": \"saved name\"}";
        mockMvc.perform(post("/teams")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andExpect(content().string("Internal Server Error: Access Denied"));
    }

    @Test
    public void testUnregisteredUserAccessDenied() throws Exception {
        String requestBody = "{\"name\": \"saved name\"}";
        mockMvc.perform(post("/teams")
                        .with(anonymous())
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "username", roles = {"ADMIN"})
    @Test
    @Sql("classpath:data/teamrepositorytests/insert-teams.sql")
    @Sql("classpath:data/teamrepositorytests/insert-users-teams.sql")
    void addUserToTeam() throws Exception {
        String requestBody = "{\"id\": \"1\"}";
        mockMvc.perform(post("/teams/1/users")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("1"));
    }

    @WithMockUser(username = "username", roles = {"ADMIN"})
    @Test
    @Sql("classpath:data/teamrepositorytests/insert-teams.sql")
    @Sql("classpath:data/teamrepositorytests/insert-users-teams.sql")
    void removeUserFromTeam() throws Exception {
        String requestBody = "{\"id\": \"1\"}";
        mockMvc.perform(delete("/teams/1/users")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}

