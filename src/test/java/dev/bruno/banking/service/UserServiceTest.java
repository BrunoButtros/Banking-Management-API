package dev.bruno.banking.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.bruno.banking.dto.UserRequestDTO;
import dev.bruno.banking.dto.UserResponseDTO;
import dev.bruno.banking.model.User;
import dev.bruno.banking.exception.EmailAlreadyRegisteredException;
import dev.bruno.banking.exception.UserNotFoundException;
import dev.bruno.banking.repository.UserRepository;
import dev.bruno.banking.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserRequestDTO userRequest;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequestDTO();
        userRequest.setName("User");
        userRequest.setEmail("user@example.com");
        userRequest.setPassword("secret");
    }

    @Test
    void createUser_ShouldThrowEmailAlreadyRegistered_WhenEmailExists() {
        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(new User()));

        EmailAlreadyRegisteredException ex = assertThrows(EmailAlreadyRegisteredException.class,
                () -> userService.createUser(userRequest));

        assertTrue(ex.getMessage().contains("Email already registered: user@example.com"));
    }

    @Test
    void createUser_ShouldCreateUser_WhenEmailDoesNotExist() {
        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("secret")).thenReturn("encodedSecret");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        UserResponseDTO result = userService.createUser(userRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("User", result.getName());
        assertEquals("user@example.com", result.getEmail());

        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("secret");
    }

    @Test
    void updateUser_ShouldThrowUserNotFound_WhenUserIdDoesNotExist() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> userService.updateUser(99L, userRequest));

        assertTrue(ex.getMessage().contains("User not found with id 99"));
    }

    @Test
    void updateUser_ShouldThrowEmailAlreadyRegistered_WhenNewEmailExistsForAnotherUser() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail("old@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setEmail("user@example.com");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(anotherUser));

        userRequest.setEmail("user@example.com");

        EmailAlreadyRegisteredException ex = assertThrows(EmailAlreadyRegisteredException.class,
                () -> userService.updateUser(1L, userRequest));

        assertTrue(ex.getMessage().contains("Email already registered: user@example.com"));
    }

    @Test
    void updateUser_ShouldUpdateSuccessfully_WhenUserExistsAndEmailIsUnique() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail("old@example.com");
        existingUser.setName("Old Name");
        existingUser.setPassword("oldPassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secret")).thenReturn("encodedSecret");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponseDTO result = userService.updateUser(1L, userRequest);

        assertNotNull(result);
        assertEquals("User", result.getName());
        assertEquals("user@example.com", result.getEmail());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void deleteUser_ShouldThrowUserNotFound_WhenUserDoesNotExist() {
        when(userRepository.existsById(99L)).thenReturn(false);

        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> userService.deleteUser(99L));

        assertTrue(ex.getMessage().contains("User not found with id 99"));
    }

    @Test
    void deleteUser_ShouldDelete_WhenUserExists() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }
}
