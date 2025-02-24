package dev.bruno.banking.service;

import dev.bruno.banking.config.CustomUserDetails;
import dev.bruno.banking.exception.UserNotFoundException;
import dev.bruno.banking.model.User;
import dev.bruno.banking.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void testLoadUserByUsername_UserFound() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("test@example.com");

        assertNotNull(userDetails);
        assertTrue(userDetails instanceof CustomUserDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Dado
        when(userRepository.findByEmail("notfound@example.com"))
                .thenReturn(Optional.empty());

        // Quando & Então
        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("notfound@example.com"));
        assertTrue(ex.getMessage().contains("User not found with email: notfound@example.com"));
    }
}
