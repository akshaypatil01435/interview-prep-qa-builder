package com.interviewprep.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest
class QuestionEndpointIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private JsonMapper jsonMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = webAppContextSetup(webApplicationContext)
                .addFilters(springSecurityFilterChain)
                .build();
    }

    private String obtainToken() throws Exception {
        String email = "candidate-" + UUID.randomUUID() + "@example.com";
        String registerRequest = "{\"fullName\":\"Candidate User\",\"email\":\"" + email + "\",\"password\":\"secure-password\"}";
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequest))
                .andExpect(status().isCreated());

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"secure-password\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return jsonMapper.readTree(loginResponse).get("token").asString();
    }

    @Test
    void getQuestionsReturnsPaginatedSeedData() throws Exception {
        String token = obtainToken();

        mockMvc.perform(get("/api/questions")
                        .header("Authorization", "Bearer " + token)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.totalElements").isNumber())
                .andExpect(jsonPath("$.content[0].questionText").isNotEmpty())
                .andExpect(jsonPath("$.content[0].topic.name").isNotEmpty());
    }

    @Test
    void getTopicsReturnsList() throws Exception {
        String token = obtainToken();

        mockMvc.perform(get("/api/topics")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").isNotEmpty());
    }

    @Test
    void solvedAndBookmarkEndpointsToggleFlags() throws Exception {
        String token = obtainToken();

        // 1. Get first question ID from questions list
        String questionsJson = mockMvc.perform(get("/api/questions")
                        .header("Authorization", "Bearer " + token)
                        .param("page", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        long questionId = jsonMapper.readTree(questionsJson).get("content").get(0).get("id").asLong();

        // 2. Mark solved
        mockMvc.perform(put("/api/questions/" + questionId + "/solved")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.solved").value(true));

        // 3. Mark bookmark
        mockMvc.perform(put("/api/questions/" + questionId + "/bookmark")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookmarked").value(true));

        // 4. Verify in bookmarks endpoint
        mockMvc.perform(get("/api/bookmarks")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(questionId))
                .andExpect(jsonPath("$[0].solved").value(true))
                .andExpect(jsonPath("$[0].bookmarked").value(true));

        // 5. Unbookmark
        mockMvc.perform(delete("/api/questions/" + questionId + "/bookmark")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookmarked").value(false));

        // 6. Mark unsolved
        mockMvc.perform(delete("/api/questions/" + questionId + "/solved")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.solved").value(false));
    }
}
