package com.simon.smile.user;

import com.simon.smile.common.exception.ObjectNotFoundException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserToUserDtoConverter userToUserDtoConverter;

    @MockBean
    private UserService userService;

    @Value("${api.base-url}/users")
    private String usersUrl;

    private List<AppUser> users;
    private AppUser admin;
    private AppUser normalUser;
    private AppUser inactiveUser;

    @BeforeEach
    void setUp() {
        admin = new AppUser()
                .setId(1)
                .setUsername("admin")
                .setEmail("admin@example.com")
                .setEnabled(true)
                .setRoles("ROLE_ADMIN");
        normalUser = new AppUser()
                .setId(2)
                .setUsername("simon")
                .setEmail("simon@smile.com")
                .setEnabled(true)
                .setRoles("ROLE_USER ROLE_CUSTOMER");
        inactiveUser = new AppUser()
                .setId(3)
                .setUsername("owen")
                .setEmail("owen@example.com")
                .setEnabled(false)
                .setRoles("ROLE_INACTIVE");
        users = List.of(admin, normalUser, inactiveUser);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Verify retrieve user by ID success when the ID exist")
    void testFindUserByIdSuccess() throws Exception {
        given(userService.findById(anyInt())).willReturn(admin);

        UserDto userDto = userToUserDtoConverter.convert(admin);

        mockMvc.perform(get(usersUrl + "/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HTTP_OK))
                .andExpect(jsonPath("$.message").value("Find user success"))
                .andExpect(jsonPath("$.data.username").value(userDto.username()))
                .andExpect(jsonPath("$.data.nickname").value(userDto.nickname()))
                .andExpect(jsonPath("$.data.email").value(userDto.email()))
                .andExpect(jsonPath("$.data.roles").value(userDto.roles()))
                .andExpect(jsonPath("$.data.enabled").value(userDto.enabled()))
                .andExpect(jsonPath("$.data.password").doesNotHaveJsonPath());
    }

    @Test
    @DisplayName("Verify retrieve user by ID error when the ID not exist")
    void testFindUserByIdErrorWhenTheIdNotExist() throws Exception {
        given(userService.findById(anyInt())).willThrow(new ObjectNotFoundException("Not found user with ID: 1"));

        mockMvc.perform(get(usersUrl + "/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HTTP_NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Not found user with ID: 1"))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()));
    }

    @Test
    @DisplayName("Verify retrieve all users success")
    void testFindAllUsersSuccess() throws Exception {
        given(userService.findAll()).willReturn(users);

        mockMvc.perform(get(usersUrl)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HTTP_OK))
                .andExpect(jsonPath("$.message").value("Find all users success"))
                .andExpect(jsonPath("$.data").value(Matchers.hasSize(users.size())));
    }
}