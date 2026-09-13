package com.interviewprep.backend;

import com.interviewprep.backend.entity.Role;
import com.interviewprep.backend.entity.User;
import com.interviewprep.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest
class AdminQuestionEndpointIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JsonMapper jsonMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = webAppContextSetup(webApplicationContext)
                .addFilters(springSecurityFilterChain)
                .build();
    }

    private String createAndLoginUser(Role role) throws Exception {
        String email = "admin-test-" + UUID.randomUUID() + "@example.com";
        User user = new User();
        user.setEmail(email);
        user.setFullName("Test " + role.name());
        user.setPassword(passwordEncoder.encode("secure-password"));
        user.setRole(role);
        userRepository.save(user);

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"secure-password\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return jsonMapper.readTree(loginResponse).get("token").asString();
    }

    @Test
    void nonAdminCannotAccessAdminEndpoints() throws Exception {
        String userToken = createAndLoginUser(Role.USER);

        // Topic creation forbidden
        mockMvc.perform(post("/api/admin/topics")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Forbidden Topic\"}"))
                .andExpect(status().isForbidden());

        // Question creation forbidden
        mockMvc.perform(post("/api/admin/questions")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"questionText\":\"Forbidden?\",\"answerText\":\"Yes\",\"difficulty\":\"EASY\",\"topicId\":1}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanPerformTopicAndQuestionCrud() throws Exception {
        String adminToken = createAndLoginUser(Role.ADMIN);

        // 1. Create Topic
        String uniqueTopicName = "Topic-" + UUID.randomUUID().toString().substring(0, 8);
        String topicResponse = mockMvc.perform(post("/api/admin/topics")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + uniqueTopicName + "\",\"description\":\"Test description\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(uniqueTopicName))
                .andReturn().getResponse().getContentAsString();

        long topicId = jsonMapper.readTree(topicResponse).get("id").asLong();

        // 2. Update Topic
        mockMvc.perform(put("/api/admin/topics/" + topicId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + uniqueTopicName + " Updated\",\"description\":\"Updated description\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(uniqueTopicName + " Updated"));

        // 3. Create Question under that topic
        String questionResponse = mockMvc.perform(post("/api/admin/questions")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"questionText\":\"What is integration testing?\",\"answerText\":\"Testing components together.\",\"difficulty\":\"MEDIUM\",\"topicId\":" + topicId + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.questionText").value("What is integration testing?"))
                .andExpect(jsonPath("$.difficulty").value("MEDIUM"))
                .andReturn().getResponse().getContentAsString();

        long questionId = jsonMapper.readTree(questionResponse).get("id").asLong();

        // 4. Update Question
        mockMvc.perform(put("/api/admin/questions/" + questionId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"questionText\":\"What is integration testing updated?\",\"answerText\":\"Testing components together updated.\",\"difficulty\":\"HARD\",\"topicId\":" + topicId + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questionText").value("What is integration testing updated?"))
                .andExpect(jsonPath("$.difficulty").value("HARD"));

        // 5. Attempt deleting topic while question exists -> Should fail with 409 Conflict
        mockMvc.perform(delete("/api/admin/topics/" + topicId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("contains 1 question(s)")));

        // 6. Delete question
        mockMvc.perform(delete("/api/admin/questions/" + questionId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        // 7. Now delete topic -> Should succeed
        mockMvc.perform(delete("/api/admin/topics/" + topicId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }
}
