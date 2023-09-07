package com.simon.smile.user;

import com.simon.smile.common.exception.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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

    public AppUser create(AppUser user) {
        return this.userRepository.save(user);
    }

    public AppUser update(Integer id, AppUser appUser) {
        findById(id);
        return userRepository.save(appUser);
    }
}
