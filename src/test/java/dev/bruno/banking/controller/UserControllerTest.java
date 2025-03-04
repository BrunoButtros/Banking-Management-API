package dev.bruno.banking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.bruno.banking.dto.UserRequestDTO;
import dev.bruno.banking.dto.UserResponseDTO;
import dev.bruno.banking.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;


    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testCreateUser() throws Exception {
        UserRequestDTO request = new UserRequestDTO();
        request.setName("User");
        request.setEmail("User@example.com");
        request.setPassword("secret");

        UserResponseDTO response = new UserResponseDTO();
        response.setId(1L);
        response.setName("User");
        response.setEmail("User@example.com");

        when(userService.createUser(any(UserRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testUpdateUser() throws Exception {
        Long userId = 1L;

        UserRequestDTO updatedUserRequest = new UserRequestDTO();
        updatedUserRequest.setName("Updated Name");
        updatedUserRequest.setEmail("updated@example.com");
        updatedUserRequest.setPassword("securePassword123");

        UserResponseDTO updatedUserResponse = new UserResponseDTO();
        updatedUserResponse.setId(userId);
        updatedUserResponse.setName("Updated Name");
        updatedUserResponse.setEmail("updated@example.com");

        when(userService.updateUser(any(Long.class), any(UserRequestDTO.class)))
                .thenReturn(updatedUserResponse);

        mockMvc.perform(put("/users/{id}", userId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }





    @Test
    void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetUserById() throws Exception {
        UserResponseDTO response = new UserResponseDTO();
        response.setId(1L);
        response.setName("User");
        response.setEmail("User@example.com");

        when(userService.getUserById(1L)).thenReturn(response);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("User@example.com"));
    }

    @Test
    void testGetUserByEmail() throws Exception {
        UserResponseDTO response = new UserResponseDTO();
        response.setId(2L);
        response.setName("User");
        response.setEmail("User@example.com");

        when(userService.getUserByEmail("User@example.com")).thenReturn(response);

        mockMvc.perform(get("/users/email")
                        .param("email", "User@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("User"));
    }
}
