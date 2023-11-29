package com.simon.smile.user;

import com.simon.smile.auth.AppUserPrincipal;
import com.simon.smile.common.exception.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.exact;
import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.ignoreCase;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public AppUser create(AppUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return this.userRepository.save(user);
    }

    public void deleteById(Integer id) {
        findById(id);
        userRepository.deleteById(id);
    }

    public List<AppUser> filter(AppUser appUser) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("id")
                .withMatcher("username", ignoreCase().contains())
                .withMatcher("nickname", ignoreCase().contains())
                .withMatcher("email", ignoreCase())
                .withMatcher("roles", ignoreCase().contains());
        if (Objects.nonNull(appUser.getEnabled())) {
            matcher = matcher.withMatcher("enabled", exact());
        }
        Example<AppUser> example = Example.of(appUser, matcher);
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        return userRepository.findAll(example, sort);
    }

    public List<AppUser> findAll() {
        return userRepository.findAll();
    }

    public Optional<AppUser> findByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public AppUser findById(Integer id) {
        return this.userRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Not found user with ID: %s", id)));
    }

    public Optional<AppUser> findByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        return userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .map(AppUserPrincipal::new)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("No user found with username or email: %s", usernameOrEmail)));
    }

    public AppUser update(Integer id, AppUser appUser) {
        findById(id);
        appUser.setId(id);
        return userRepository.save(appUser);
    }
}
