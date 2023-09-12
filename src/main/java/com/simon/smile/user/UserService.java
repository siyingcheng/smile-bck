package com.simon.smile.user;

import com.simon.smile.common.exception.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.exact;
import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.ignoreCase;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AppUser create(AppUser user) {
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
                .withMatcher("enabled", exact())
                .withMatcher("roles", ignoreCase().contains());
        Example<AppUser> example = Example.of(appUser, matcher);
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        return userRepository.findAll(example, sort);
    }

    public List<AppUser> findAll() {
        return userRepository.findAll();
    }

    public AppUser findById(Integer id) {
        return this.userRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Not found user with ID: %s", id)));
    }

    public AppUser findByUsername(String username) {
        return this.userRepository.findByUsername(username)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Not found user with username: %s", username)));
    }

    public AppUser update(Integer id, AppUser appUser) {
        findById(id);
        return userRepository.save(appUser);
    }
}
