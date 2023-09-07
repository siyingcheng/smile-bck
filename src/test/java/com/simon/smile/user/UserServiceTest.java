package com.simon.smile.user;

import com.simon.smile.common.exception.ObjectNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    private List<AppUser> users;
    private AppUser admin;

    @BeforeEach
    void setUp() {
        admin = new AppUser()
                .setId(1)
                .setUsername("admin")
                .setEmail("admin@example.com")
                .setEnabled(true)
                .setRoles("ROLE_ADMIN");
        AppUser normalUser = new AppUser()
                .setId(2)
                .setUsername("simon")
                .setEmail("simon@smile.com")
                .setEnabled(true)
                .setRoles("ROLE_USER ROLE_CUSTOMER");
        AppUser inactiveUser = new AppUser()
                .setId(3)
                .setUsername("owen")
                .setEmail("owen@example.com")
                .setEnabled(false)
                .setRoles("ROLE_INACTIVE");
        users = List.of(admin, normalUser, inactiveUser);
    }

    @Test
    @DisplayName("Verify find user by ID success when the ID exist")
    void findByIdSuccess() {
        given(userRepository.findById(anyInt())).willReturn(Optional.of(admin));

        AppUser foundUser = userService.findById(1);

        assertThat(foundUser.getUsername()).isEqualTo(admin.getUsername());
        assertThat(foundUser.getNickname()).isEqualTo(admin.getNickname());
        assertThat(foundUser.getEmail()).isEqualTo(admin.getEmail());
        assertThat(foundUser.getRoles()).isEqualTo(admin.getRoles());
        assertThat(foundUser.getPassword()).isEqualTo(admin.getPassword());
        assertThat(foundUser.isEnabled()).isEqualTo(admin.isEnabled());
        verify(userRepository, times(1)).findById(anyInt());
    }

    @Test
    @DisplayName("Verify find user by ID error when the ID not exist")
    void findByIdNotFound() {
        given(userRepository.findById(anyInt())).willReturn(Optional.empty());

        Throwable throwable = catchThrowable(() -> userService.findById(1));
        assertThat(throwable).isInstanceOf(ObjectNotFoundException.class);
        assertThat(throwable.getMessage()).isEqualTo("Not found user with ID: 1");
    }

    @Test
    @DisplayName("Verify find user by username success when the username exist")
    void findByUsernameSuccess() {
        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(admin));

        AppUser foundUser = userService.findByUsername("admin");

        assertThat(foundUser.getUsername()).isEqualTo(admin.getUsername());
        assertThat(foundUser.getNickname()).isEqualTo(admin.getNickname());
        assertThat(foundUser.getEmail()).isEqualTo(admin.getEmail());
        assertThat(foundUser.getRoles()).isEqualTo(admin.getRoles());
        assertThat(foundUser.getPassword()).isEqualTo(admin.getPassword());
        assertThat(foundUser.isEnabled()).isEqualTo(admin.isEnabled());
        verify(userRepository, times(1)).findByUsername(anyString());
    }

    @Test
    @DisplayName("Verify find user by username error when the username not exist")
    void findByUsernameErrorWhenUsernameNotExist() {
        given(userRepository.findByUsername(anyString())).willReturn(Optional.empty());

        Throwable throwable = catchThrowable(() -> userService.findByUsername("unknown"));

        assertThat(throwable).isInstanceOf(ObjectNotFoundException.class);
        assertThat(throwable.getMessage()).isEqualTo("Not found user with username: unknown");
    }

    @Test
    @DisplayName("Verify find all users success")
    void findAllUsersSuccess() {
        given(userRepository.findAll()).willReturn(users);

        List<AppUser> foundUsers = userService.findAll();

        assertThat(foundUsers).isEqualTo(users);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Verify create user success")
    void createUserSuccess() {
        AppUser testUser = new AppUser()
                .setUsername("Titian")
                .setNickname("Tessa Rodriguez")
                .setEmail("mohammed.silva@example.com")
                .setRoles("ROLE_USER")
                .setEnabled(true);

        given(userRepository.save(any(AppUser.class))).willReturn(testUser);

        AppUser createdUser = userService.create(testUser);

        assertThat(createdUser.getUsername()).isEqualTo(testUser.getUsername());
        assertThat(createdUser.getNickname()).isEqualTo(testUser.getNickname());
        assertThat(createdUser.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(createdUser.getRoles()).isEqualTo(testUser.getRoles());
        assertThat(createdUser.isEnabled()).isEqualTo(testUser.isEnabled());
        verify(userRepository, times(1)).save(any(AppUser.class));
    }

    @Test
    @DisplayName("Verify update user success")
    void updateUserSuccess() {
        AppUser testUser = new AppUser()
                .setId(1)
                .setUsername("Titian")
                .setNickname("Tessa Rodriguez")
                .setEmail("mohammed.silva@example.com")
                .setRoles("ROLE_USER")
                .setEnabled(true);
        AppUser newUser = new AppUser()
                .setUsername("Armand")
                .setNickname("Gabriel Hills")
                .setEmail("juliet.edwards@example.com")
                .setRoles("ROLE_ADMIN")
                .setEnabled(false);

        given(userRepository.findById(anyInt())).willReturn(Optional.of(testUser));
        given(userRepository.save(any(AppUser.class))).willReturn(newUser);

        AppUser updatedUser = userService.update(1, newUser);

        assertThat(updatedUser.getUsername()).isEqualTo(newUser.getUsername());
        assertThat(updatedUser.getNickname()).isEqualTo(newUser.getNickname());
        assertThat(updatedUser.getEmail()).isEqualTo(newUser.getEmail());
        assertThat(updatedUser.getRoles()).isEqualTo(newUser.getRoles());
        assertThat(updatedUser.isEnabled()).isEqualTo(newUser.isEnabled());
        verify(userRepository, times(1)).findById(anyInt());
        verify(userRepository, times(1)).save(any(AppUser.class));
    }

    @Test
    @DisplayName("Verify update user error when ID not exist")
    void updateUserErrorWhenIdNotExist() {
        AppUser newUser = new AppUser()
                .setUsername("Armand")
                .setNickname("Gabriel Hills")
                .setEmail("juliet.edwards@example.com")
                .setRoles("ROLE_ADMIN")
                .setEnabled(false);

        given(userRepository.findById(anyInt())).willReturn(Optional.empty());

        Throwable throwable = catchThrowable(() -> userService.update(1, newUser));

        assertThat(throwable).isInstanceOf(ObjectNotFoundException.class);
        assertThat(throwable.getMessage()).isEqualTo("Not found user with ID: 1");
        verify(userRepository, times(1)).findById(anyInt());
        verify(userRepository, times(0)).save(any(AppUser.class));
    }
}