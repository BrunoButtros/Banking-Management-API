package dev.bruno.banking.repository;

import dev.bruno.banking.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class UserRepositoryTest {


    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setName("user");
        user.setEmail("user@example.com");
        user.setPassword("password");

        user = userRepository.save(user);
    }

    @Test
    void shouldSaveUser() {
        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId());
        assertEquals("user", savedUser.getName());
        assertEquals("user@example.com", savedUser.getEmail());
    }

    @Test
    void shouldFindUserByEmail() {
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByEmail("user@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals("user", foundUser.get().getName());
    }

    @Test
    void shouldReturnEmptyWhenEmailNotExists() {
        Optional<User> foundUser = userRepository.findByEmail("notfound@example.com");

        assertTrue(foundUser.isEmpty());
    }

    @Test
    void shouldDeleteUser() {
        User savedUser = userRepository.save(user);
        userRepository.delete(savedUser);

        entityManager.flush();
        entityManager.clear();

        Optional<User> deletedUser = userRepository.findByEmail("user@example.com");

        assertTrue(deletedUser.isEmpty());
    }

    @Test
    void shouldUpdateUserEmail() {
        user.setEmail("updated@example.com");
        userRepository.save(user); // Salva a alteração

        entityManager.flush();
        entityManager.clear(); // Evita pegar dados do cache do JPA

        Optional<User> updatedUser = userRepository.findByEmail("updated@example.com");

        assertTrue(updatedUser.isPresent());
        assertEquals("updated@example.com", updatedUser.get().getEmail());
    }
}

