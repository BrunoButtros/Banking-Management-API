package dev.bruno.banking.service;


import dev.bruno.banking.model.User;
import dev.bruno.banking.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private UserRepository userRepository;

    public Optional<User> findById(Long Id) {
        return userRepository.findById(Id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }
}
