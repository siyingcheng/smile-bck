package com.simon.smile.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simon.smile.common.exception.ObjectNotFoundException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.security.InvalidParameterException;
import java.util.List;

import static com.simon.smile.common.Constant.DEFAULT_PASSWORD;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
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
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
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
                .setRoles("ROLE_USER ROLE_CONSUMER");
        users = List.of(admin, normalUser, inactiveUser);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Verify create user error when password not match rules")
    void testCreateUserErrorWhenPasswordNotMatchRules() throws Exception {
        AppUser appUser = new AppUser()
                .setUsername("Katerine")
                .setNickname("Florencio Baumbach")
                .setPassword("Pass@Word")
                .setEmail("beryl.travis@example.com")
                .setRoles("ROLE_USER")
                .setEnabled(true);

        final String errorMessage = "Password is not strong enough; 1. At least a number; 2. A least a lower letter; 3. At least a upper letter; 4. No spaces; 5. At least 8 characters, at most 20 characters";

        // password no number
        mockMvc.perform(post(usersUrl)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()));

        // password no lower letter
        appUser.setPassword("AUTOMATION123");
        mockMvc.perform(post(usersUrl)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()));

        // password no upper letter
        appUser.setPassword("automation123");
        mockMvc.perform(post(usersUrl)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()));

        // password has space
        appUser.setPassword("A automation123");
        mockMvc.perform(post(usersUrl)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()));

        // length too short
        appUser.setPassword("Auto123");
        mockMvc.perform(post(usersUrl)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()));

        // length too long
        appUser.setPassword("ThisIsTooLongPassWord123");
        mockMvc.perform(post(usersUrl)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()));

        // password is null
        appUser.setPassword(null);
        mockMvc.perform(post(usersUrl)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value("password is required"))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()));
    }

    @Test
    @DisplayName("Verify create user error when the email already exist")
    void testCreateUserErrorWhenTheEmailAlreadyExist() throws Exception {
        given(userService.findByEmail(admin.getEmail()))
                .willThrow(new InvalidParameterException("email already exist"));

        mockMvc.perform(post(usersUrl)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(admin.setPassword(DEFAULT_PASSWORD))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value("email already exist"))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()));
    }

    @Test
    @DisplayName("Verify create user error when the user attributes are incorrect")
    void testCreateUserErrorWhenTheUserAttributesIncorrect() throws Exception {
        AppUser appUser = new AppUser()
                .setPassword("NormalPassw0rd")
                .setRoles("ROLE_USER")
                .setEnabled(true);

        mockMvc.perform(post(usersUrl)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value("Provided arguments are invalid, set data for details"))
                .andExpect(jsonPath("$.data.email").value("email is required"))
                .andExpect(jsonPath("$.data.username").value("username is required"));

        appUser.setUsername("us") // too short
                .setEmail("russ.acevedo"); // invalid email
        mockMvc.perform(post(usersUrl)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value("Provided arguments are invalid, set data for details"))
                .andExpect(jsonPath("$.data.email").value("email format is invalid"))
                .andExpect(jsonPath("$.data.username").value("username length must between 3 and 16"));

        appUser.setUsername("thisIsTooLongUsername12345678") // too long
                .setNickname("ThisNickNameAlsoTooLongMoreThan32ThisNickNameAlsoTooLongMoreThan32") // too long
                .setEmail("russ.acevedo@mail.com");
        mockMvc.perform(post(usersUrl)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value("Provided arguments are invalid, set data for details"))
                .andExpect(jsonPath("$.data.username").value("username length must between 3 and 16"))
                .andExpect(jsonPath("$.data.nickname").value("nickname length must between 0 and 32"));
    }

    @Test
    @DisplayName("Verify create user error when the username already exist")
    void testCreateUserErrorWhenTheUsernameAlreadyExist() throws Exception {
        given(userService.findByUsername(admin.getUsername()))
                .willThrow(new InvalidParameterException("username already exist"));

        mockMvc.perform(post(usersUrl)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(admin.setPassword(DEFAULT_PASSWORD))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value("username already exist"))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()));
    }

    @Test
    @DisplayName("Verify create user success")
    void testCreateUserSuccess() throws Exception {
        AppUser appUser = new AppUser()
                .setUsername("Katerine")
                .setNickname("Florencio Baumbach")
                .setPassword("Pass@W0rd")
                .setEmail("beryl.travis@example.com")
                .setRoles("ROLE_USER")
                .setEnabled(true);

        given(userService.create(any(AppUser.class))).willReturn(appUser);

        mockMvc.perform(post(usersUrl)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.message").value("Create user success"))
                .andExpect(jsonPath("$.data.username").value(appUser.getUsername()))
                .andExpect(jsonPath("$.data.nickname").value(appUser.getNickname()))
                .andExpect(jsonPath("$.data.email").value(appUser.getEmail()))
                .andExpect(jsonPath("$.data.roles").value(appUser.getRoles()))
                .andExpect(jsonPath("$.data.enabled").value(appUser.isEnabled()))
                .andExpect(jsonPath("$.data.password").doesNotHaveJsonPath());
    }

    @Test
    @DisplayName("Verify delete user error when user id not exist")
    void testDeleteUserByIdErrorWhenUserIdNotExist() throws Exception {

        doThrow(new ObjectNotFoundException("Not found user with ID: 1"))
                .when(userService).deleteById(anyInt());

        mockMvc.perform(delete(usersUrl + "/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value("Not found user with ID: 1"))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()));
    }

    @Test
    @DisplayName("Verify delete user success")
    void testDeleteUserByIdSuccess() throws Exception {

        doNothing().when(userService).deleteById(anyInt());

        mockMvc.perform(delete(usersUrl + "/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.message").value("Delete user success"))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()));
    }

    @Test
    @DisplayName("Verify filter users success")
    void testFilterUsersSuccess() throws Exception {
        // filter by username
        var appUser = new AppUser().setUsername("ad");
        given(userService.filter(any(AppUser.class))).willReturn(List.of(admin));
        mockMvc.perform(post(usersUrl + "/filter")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.message").value("Find user(s) success"))
                .andExpect(jsonPath("$.data").value(Matchers.hasSize(1)))
                .andExpect(jsonPath("$.data[0].username").value(admin.getUsername()));

        // filter by email
        appUser = new AppUser().setEmail("@example");
        given(userService.filter(any(AppUser.class))).willReturn(List.of(admin, inactiveUser));
        mockMvc.perform(post(usersUrl + "/filter")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.message").value("Find user(s) success"))
                .andExpect(jsonPath("$.data").value(Matchers.hasSize(2)))
                .andExpect(jsonPath("$.data[0].username").value(admin.getUsername()))
                .andExpect(jsonPath("$.data[1].username").value(inactiveUser.getUsername()));

        // filter by enabled
        appUser = new AppUser().setEnabled(true);
        given(userService.filter(any(AppUser.class))).willReturn(List.of(admin, normalUser));
        mockMvc.perform(post(usersUrl + "/filter")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.message").value("Find user(s) success"))
                .andExpect(jsonPath("$.data").value(Matchers.hasSize(2)))
                .andExpect(jsonPath("$.data[0].username").value(admin.getUsername()))
                .andExpect(jsonPath("$.data[1].username").value(normalUser.getUsername()));

        // filter by roles
        appUser = new AppUser().setRoles("ROLE_USER");
        given(userService.filter(any(AppUser.class))).willReturn(List.of(normalUser, inactiveUser));
        mockMvc.perform(post(usersUrl + "/filter")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.message").value("Find user(s) success"))
                .andExpect(jsonPath("$.data").value(Matchers.hasSize(2)))
                .andExpect(jsonPath("$.data[0].username").value(normalUser.getUsername()))
                .andExpect(jsonPath("$.data[1].username").value(inactiveUser.getUsername()));

        // filter by enabled and roles
        appUser = new AppUser().setEnabled(true).setRoles("ROLE_USER");
        given(userService.filter(any(AppUser.class))).willReturn(List.of(normalUser));
        mockMvc.perform(post(usersUrl + "/filter")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.message").value("Find user(s) success"))
                .andExpect(jsonPath("$.data").value(Matchers.hasSize(1)))
                .andExpect(jsonPath("$.data[0].username").value(normalUser.getUsername()));

        // filter by enabled and roles
        appUser = new AppUser().setUsername("words").setEnabled(false).setRoles("ROLE_ADMIN");
        given(userService.filter(any(AppUser.class))).willReturn(List.of());
        mockMvc.perform(post(usersUrl + "/filter")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.message").value("Find user(s) success"))
                .andExpect(jsonPath("$.data").value(Matchers.hasSize(0)));
    }

    @Test
    @DisplayName("Verify retrieve all users success")
    void testFindAllUsersSuccess() throws Exception {
        given(userService.findAll()).willReturn(users);

        mockMvc.perform(get(usersUrl)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.message").value("Find all users success"))
                .andExpect(jsonPath("$.data").value(Matchers.hasSize(users.size())));
    }

    @Test
    @DisplayName("Verify retrieve user by ID error when the ID not exist")
    void testFindUserByIdErrorWhenTheIdNotExist() throws Exception {
        given(userService.findById(anyInt())).willThrow(new ObjectNotFoundException("Not found user with ID: 1"));

        mockMvc.perform(get(usersUrl + "/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value("Not found user with ID: 1"))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()));
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
                .andExpect(jsonPath("$.message").value("Find user success"))
                .andExpect(jsonPath("$.data.username").value(userDto.username()))
                .andExpect(jsonPath("$.data.nickname").value(userDto.nickname()))
                .andExpect(jsonPath("$.data.email").value(userDto.email()))
                .andExpect(jsonPath("$.data.roles").value(userDto.roles()))
                .andExpect(jsonPath("$.data.enabled").value(userDto.enabled()))
                .andExpect(jsonPath("$.data.password").doesNotHaveJsonPath());
    }

    @Test
    @DisplayName("Verify update user error when ID not exist")
    void testUpdateUserByIdErrorWhenIdNotExist() throws Exception {
        AppUser appUser = new AppUser()
                .setId(1)
                .setUsername("Katerine")
                .setNickname("Florencio Baumbach")
                .setPassword("Pass@W0rd")
                .setEmail("beryl.travis@example.com")
                .setRoles("ROLE_USER")
                .setEnabled(true);

        given(userService.update(anyInt(), any(AppUser.class)))
                .willThrow(new ObjectNotFoundException("Not found user with ID: 1"));

        mockMvc.perform(put(usersUrl + "/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appUser)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value("Not found user with ID: 1"))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()));
    }

    @Test
    @DisplayName("Verify update user error when user attributes are not valid")
    void testUpdateUserByIdErrorWhenUserAttributesAreNotValid() throws Exception {
        AppUser appUser = new AppUser()
                .setId(1)
                .setRoles("ROLE_USER")
                .setEnabled(true);

        given(userService.findById(anyInt())).willReturn(admin);

        mockMvc.perform(put(usersUrl + "/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value("Provided arguments are invalid, set data for details"))
                .andExpect(jsonPath("$.data.email").value("email is required"))
                .andExpect(jsonPath("$.data.username").value("username is required"));
    }

    @Test
    @DisplayName("Verify update user success")
    void testUpdateUserByIdSuccess() throws Exception {
        AppUser appUser = new AppUser()
                .setUsername("Katerine")
                .setNickname("Florencio Baumbach")
                .setPassword("Pass@W0rd")
                .setEmail("beryl.travis@example.com")
                .setRoles("ROLE_USER")
                .setEnabled(true);

        given(userService.update(anyInt(), any(AppUser.class))).willReturn(appUser);

        mockMvc.perform(put(usersUrl + "/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.message").value("Update user success"))
                .andExpect(jsonPath("$.data.username").value(appUser.getUsername()))
                .andExpect(jsonPath("$.data.nickname").value(appUser.getNickname()))
                .andExpect(jsonPath("$.data.email").value(appUser.getEmail()))
                .andExpect(jsonPath("$.data.roles").value(appUser.getRoles()))
                .andExpect(jsonPath("$.data.enabled").value(appUser.isEnabled()))
                .andExpect(jsonPath("$.data.password").doesNotHaveJsonPath());
    }
}