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
public class CommentControllerTest {
    private MockMvc mockMvc;

    @BeforeEach
    void setUp(WebApplicationContext wac) {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity()).build();
    }

    @WithMockUser(username = "username", roles = {"USER"})
    @Test
    @Sql("classpath:data/commentrepositorytests/insert-comments.sql")
    void testGetAllComments() throws Exception {
        mockMvc.perform(get("/projects/1/tasks/1/comments")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].text").value("text"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @WithMockUser(username = "username", roles = {"USER"})
    @Test
    @Sql("classpath:data/commentrepositorytests/insert-comments.sql")
    void testGetCommentById() throws Exception {
        mockMvc.perform(get("/projects/1/tasks/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.text").value("text"));

    }

    @WithMockUser(username = "username", roles = {"ADMIN"})
    @Test
    @Sql("classpath:data/taskrepositorytests/insert-tasks.sql")
    void testSaveComment() throws Exception {
        String requestBody = "{\"text\": \"saveText\", \"userId\":\"1\"}";
        mockMvc.perform(post("/projects/1/tasks/1/comments")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.text").value("saveText"));
    }

    @WithMockUser(username = "username", roles = {"USER"})
    @Test
    @Sql("classpath:data/commentrepositorytests/insert-comments.sql")
    void testUpdateComment() throws Exception {
        String requestBody = "{\"text\": \"updated text\", \"addtime\":\"2024-04-25 20:01:46.488778\"}";
        mockMvc.perform(put("/projects/1/tasks/1/comments/1")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.text").value("updated text"));
    }

    @WithMockUser(username = "username", roles = {"USER"})
    @Test
    @Sql("classpath:data/commentrepositorytests/insert-comments.sql")
    void testDeleteComment() throws Exception {
        mockMvc.perform(delete("/projects/1/tasks/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "username", roles = {"MANAGER"})
    @Test
    public void testAccessDenied() throws Exception {
        String requestBody = "{\"title\": \"Attachment\",\"path\": \"path\"}";
        mockMvc.perform(post("/projects/1/tasks/1/attachments")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andExpect(content().string("Internal Server Error: Access Denied"));
    }

    @Test
    public void testUnregisteredUserAccessDenied() throws Exception {
        String requestBody = "{\"text\": \"saveText\", \"addtime\":\"2024-04-25 20:01:46.488778\"}";
        mockMvc.perform(post("/projects/1/tasks/1/comments")
                        .with(anonymous())
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
