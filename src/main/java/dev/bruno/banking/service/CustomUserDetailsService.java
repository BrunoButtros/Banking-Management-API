package dev.bruno.banking.service;

import dev.bruno.banking.config.CustomUserDetails;
import dev.bruno.banking.exception.UserNotFoundException;
import dev.bruno.banking.model.User;
import dev.bruno.banking.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + username));
        return new CustomUserDetails(user);
    }
}
