package com.simon.smile.user;

import com.simon.smile.common.exception.ObjectNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.exact;
import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.ignoreCase;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;
    private List<AppUser> users;
    private AppUser admin;
    private AppUser normalUser;
    private AppUser inactiveUser;

    @Test
    @DisplayName("Verify create user success")
    void createUserSuccess() {
        AppUser testUser = new AppUser()
                .setUsername("Titian")
                .setNickname("Tessa Rodriguez")
                .setEmail("mohammed.silva@example.com")
                .setRoles(Roles.ROLE_USER.getRole())
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
    @DisplayName("Verify delete user error when ID not exist")
    void deleteUserErrorWhenIdNotExist() {
        given(userRepository.findById(1)).willThrow(new ObjectNotFoundException("Not fount user with ID: 1"));

        Throwable throwable = catchThrowable(() -> userService.deleteById(1));

        assertThat(throwable).isInstanceOf(ObjectNotFoundException.class);
        assertThat(throwable.getMessage()).isEqualTo("Not fount user with ID: 1");
        verify(userRepository, times(1)).findById(anyInt());
    }

    @Test
    @DisplayName("Verify delete user success")
    void deleteUserSuccess() {
        AppUser testUser = new AppUser()
                .setId(1)
                .setUsername("Titian")
                .setNickname("Tessa Rodriguez")
                .setEmail("mohammed.silva@example.com")
                .setRoles(Roles.ROLE_USER.getRole())
                .setEnabled(true);

        given(userRepository.findById(anyInt())).willReturn(Optional.of(testUser));
        doNothing().when(userRepository).deleteById(anyInt());

        userService.deleteById(1);

        verify(userRepository, times(1)).findById(anyInt());
        verify(userRepository, times(1)).deleteById(anyInt());
    }

    @Test
    @DisplayName("Verify filter users success")
    void filterUsersSuccess() {
        AppUser appUser = new AppUser();
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("id")
                .withMatcher("username", ignoreCase().contains())
                .withMatcher("nickname", ignoreCase().contains())
                .withMatcher("email", ignoreCase())
                .withMatcher("enabled", exact())
                .withMatcher("roles", ignoreCase().contains());
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Example<AppUser> example = Example.of(appUser, matcher);

        // filter by username
        appUser.setUsername("ad");
        given(userRepository.findAll(example, sort)).willReturn(List.of(admin));
        List<AppUser> filteredUsers = userService.filter(appUser);
        assertThat(filteredUsers).hasSize(1);
        assertThat(filteredUsers.get(0).getUsername()).isEqualTo(admin.getUsername());
        verify(userRepository, times(1)).findAll(example, sort);

        // filter by email
        appUser.setUsername(null).setEmail("@example");
        given(userRepository.findAll(example, sort)).willReturn(List.of(admin, inactiveUser));
        filteredUsers = userService.filter(appUser);
        assertThat(filteredUsers).hasSize(2);
        assertThat(filteredUsers.get(0).getUsername()).isEqualTo(admin.getUsername());
        assertThat(filteredUsers.get(1).getUsername()).isEqualTo(inactiveUser.getUsername());

        // filter by enabled
        appUser.setEmail(null).setEnabled(true);
        given(userRepository.findAll(example, sort)).willReturn(List.of(admin, normalUser));
        filteredUsers = userService.filter(appUser);
        assertThat(filteredUsers).hasSize(2);
        assertThat(filteredUsers.get(0).getUsername()).isEqualTo(admin.getUsername());
        assertThat(filteredUsers.get(1).getUsername()).isEqualTo(normalUser.getUsername());

        // filter by roles
        appUser = new AppUser().setRoles(Roles.ROLE_USER.getRole());
        example = Example.of(appUser, matcher);
        given(userRepository.findAll(example, sort)).willReturn(List.of(normalUser, inactiveUser));
        filteredUsers = userService.filter(appUser);
        assertThat(filteredUsers).hasSize(2);
        assertThat(filteredUsers.get(0).getUsername()).isEqualTo(normalUser.getUsername());
        assertThat(filteredUsers.get(1).getUsername()).isEqualTo(inactiveUser.getUsername());

        // filter by enabled and roles
        appUser = new AppUser().setEnabled(true).setRoles(Roles.ROLE_USER.getRole());
        example = Example.of(appUser, matcher);
        given(userRepository.findAll(example, sort)).willReturn(List.of(normalUser));
        filteredUsers = userService.filter(appUser);
        assertThat(filteredUsers).hasSize(1);
        assertThat(filteredUsers.get(0).getUsername()).isEqualTo(normalUser.getUsername());

        // filter by enabled and roles
        appUser = new AppUser().setUsername("words").setEnabled(false).setRoles(Roles.ROLE_ADMIN.getRole());
        example = Example.of(appUser, matcher);
        given(userRepository.findAll(example, sort)).willReturn(List.of());
        filteredUsers = userService.filter(appUser);
        assertThat(filteredUsers).isEmpty();
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
    @DisplayName("Verify find user by email is not present when the email not exist")
    void findByEmailErrorWhenUsernameNotExist() {
        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());

        Optional<AppUser> unknown = userService.findByEmail("unknown@example.com");

        assertThat(unknown).isNotPresent();
        verify(userRepository, times(1)).findByEmail(anyString());
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
    @DisplayName("Verify find user by username is not present when the username not exist")
    void findByUsernameErrorWhenUsernameNotExist() {
        given(userRepository.findByUsername(anyString())).willReturn(Optional.empty());

        Optional<AppUser> unknown = userService.findByUsername("unknown");

        assertThat(unknown).isNotPresent();
        verify(userRepository, times(1)).findByUsername("unknown");
    }

    @Test
    @DisplayName("Verify find user by username success when the username exist")
    void findByUsernameSuccess() {
        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(admin));

        Optional<AppUser> foundUserOptional = userService.findByUsername("admin");

        assertThat(foundUserOptional).isPresent();
        AppUser foundUser = foundUserOptional.get();
        assertThat(foundUser.getUsername()).isEqualTo(admin.getUsername());
        assertThat(foundUser.getNickname()).isEqualTo(admin.getNickname());
        assertThat(foundUser.getEmail()).isEqualTo(admin.getEmail());
        assertThat(foundUser.getRoles()).isEqualTo(admin.getRoles());
        assertThat(foundUser.getPassword()).isEqualTo(admin.getPassword());
        assertThat(foundUser.isEnabled()).isEqualTo(admin.isEnabled());
        verify(userRepository, times(1)).findByUsername(anyString());
    }

    @Test
    @DisplayName("Verify find user by email success when the email exist")
    void findUserByEmailSuccess() {
        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(admin));

        Optional<AppUser> foundUserOptional = userService.findByEmail(admin.getEmail());

        assertThat(foundUserOptional).isPresent();
        AppUser foundUser = foundUserOptional.get();
        assertThat(foundUser.getUsername()).isEqualTo(admin.getUsername());
        assertThat(foundUser.getNickname()).isEqualTo(admin.getNickname());
        assertThat(foundUser.getEmail()).isEqualTo(admin.getEmail());
        assertThat(foundUser.getRoles()).isEqualTo(admin.getRoles());
        assertThat(foundUser.getPassword()).isEqualTo(admin.getPassword());
        assertThat(foundUser.isEnabled()).isEqualTo(admin.isEnabled());
        verify(userRepository, times(1)).findByEmail(anyString());
    }

    @BeforeEach
    void setUp() {
        admin = new AppUser()
                .setId(1)
                .setUsername("admin")
                .setEmail("admin@example.com")
                .setEnabled(true)
                .setRoles(Roles.ROLE_ADMIN.getRole());
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

    @Test
    @DisplayName("Verify update user error when ID not exist")
    void updateUserErrorWhenIdNotExist() {
        AppUser newUser = new AppUser()
                .setUsername("Armand")
                .setNickname("Gabriel Hills")
                .setEmail("juliet.edwards@example.com")
                .setRoles(Roles.ROLE_ADMIN.getRole())
                .setEnabled(false);

        given(userRepository.findById(anyInt())).willReturn(Optional.empty());

        Throwable throwable = catchThrowable(() -> userService.update(1, newUser));

        assertThat(throwable).isInstanceOf(ObjectNotFoundException.class);
        assertThat(throwable.getMessage()).isEqualTo("Not found user with ID: 1");
        verify(userRepository, times(1)).findById(anyInt());
        verify(userRepository, times(0)).save(any(AppUser.class));
    }

    @Test
    @DisplayName("Verify update user success")
    void updateUserSuccess() {
        AppUser testUser = new AppUser()
                .setId(1)
                .setUsername("Titian")
                .setNickname("Tessa Rodriguez")
                .setEmail("mohammed.silva@example.com")
                .setRoles(Roles.ROLE_USER.getRole())
                .setEnabled(true);
        AppUser newUser = new AppUser()
                .setUsername("Armand")
                .setNickname("Gabriel Hills")
                .setEmail("juliet.edwards@example.com")
                .setRoles(Roles.ROLE_ADMIN.getRole())
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
}