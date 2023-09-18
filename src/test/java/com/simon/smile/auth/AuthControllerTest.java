package com.simon.smile.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.simon.smile.common.Person.ADMIN;
import static com.simon.smile.common.Person.INVALID;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Verify authentication and authorization")
@Tag("integration")
public class AuthControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${api.base-url}")
    String baseUrl;

    @Test
    @DisplayName("Verify login error when user is disabled")
    void testLoginErrorWhenUserIsDisabled() throws Exception {
        mockMvc.perform(post(baseUrl + "/login")
                        .with(httpBasic(INVALID.username(), INVALID.password()))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message")
                        .value("user account is abnormal"))
                .andExpect(jsonPath("$.data")
                        .value("User is disabled"));
    }

    @Test
    @DisplayName("Verify login error when username or password incorrect")
    void testLoginErrorWhenUsernameOrPasswordIncorrect() throws Exception {
        // password incorrect
        mockMvc.perform(post(baseUrl + "/login")
                        .with(httpBasic(ADMIN.username(), "error_password"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value("username or password is incorrect"))
                .andExpect(jsonPath("$.data").value("Bad credentials"));

        // username incorrect
        mockMvc.perform(post(baseUrl + "/login")
                        .with(httpBasic("none_exist", "error_password"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value("username or password is incorrect"))
                .andExpect(jsonPath("$.data").value("Bad credentials"));
    }

    @Test
    @DisplayName("Verify login error without basic authentication")
    void testLoginErrorWithoutBasicAuthentication() throws Exception {
        mockMvc.perform(post(baseUrl + "/login")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value("username and password are mandatory"))
                .andExpect(jsonPath("$.data")
                        .value("Full authentication is required to access this resource"));
    }

    @Test
    @DisplayName("Verify login success")
    void testLoginSuccess() throws Exception {
        mockMvc.perform(post(baseUrl + "/login")
                        .with(httpBasic(ADMIN.username(), ADMIN.password()))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.message").value("welcome " + ADMIN.username()))
                .andExpect(jsonPath("$.data.userInfo.username").value(ADMIN.username()))
                .andExpect(jsonPath("$.data.token")
                        .value(Matchers.matchesPattern("^([0-9a-zA-Z\\-_])+\\.([0-9a-zA-Z\\-_])+\\.([0-9a-zA-Z\\-_])+$")));
    }
}
