package com.simon.smile.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.simon.smile.common.Person.ADMIN;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Verify user controller integration")
@Tag("integration")
public class UserControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${api.base-url}")
    String baseUrl;

    @Test
    @DisplayName("Verify retrieve current user information error when bearer token is invalid")
    void testRetrieveCurrentUserErrorWhenBearerTokenIsInvalid() throws Exception {
        String invalidToken = "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJzZWxmIiwic3ViIjoiYWRtaW4iLCJleHAiOjE2OTc1MTc2ODgsImlhdCI6MTY5NzUxMDQ4OCwiYXV0aG9yaXRpZXMiOiJST0xFX0FETUlOIn0.RspwgnunkRERlsdtCFqbbkYGm_vvl5O1_kaDzElCT99LNpXcANhohG2WgoVf2nd9mpOyOkEodtXgUp4ICzybmMkeyCFhHzBJhaHQ_1ewYNmmtdBcNd9pQcWGWJfQkyqRbAu0i1C043IeBsQsXBfUoWYZUXlX-0dPufP8Hl1Ds-Ea7WuYWyvNQUPrBijoiH0Hqc-IzGlPdCHXzEaqLjpIT0o8BcZfRFfJHvciSCLVO0wsPf5zhZdTYiN7KuySRUpXkYfPqBsZH7P0jepnrs6tKcU2HVkQpPypl71B7tYrQo2ancnfu7UP0RznUHqYlEXJCtWsLQPkKSDKfOpedHV6AQ";

        mockMvc.perform(get(baseUrl + "/users/current_user")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + invalidToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value("The access token provided is expired, revoked, malformed, or invalid for other reasons"))
                .andExpect(jsonPath("$.data").value("An error occurred while attempting to decode the Jwt: Signed JWT rejected: Invalid signature"));
    }

    @Test
    @DisplayName("Verify retrieve current user information success")
    void testRetrieveCurrentUserSuccess() throws Exception {
        String responseContent = mockMvc.perform(post(baseUrl + "/login")
                        .with(httpBasic(ADMIN.username(), ADMIN.password()))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String token = new JSONObject(responseContent).getJSONObject("data").getString("token");

        mockMvc.perform(get(baseUrl + "/users/current_user")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.message").value("Retrieve current user success"))
                .andExpect(jsonPath("$.data.username").value(ADMIN.username()));
    }
}
