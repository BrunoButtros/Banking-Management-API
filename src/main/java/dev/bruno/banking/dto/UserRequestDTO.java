package dev.bruno.banking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDTO {

    @NotBlank(message = "Name is required.")
    @Size(max = 100, message = "Name cannot exceed 100 characters.")
    private String name;

    @NotBlank(message = "Email is required.")
    @Email(message = "Email must be in a valid format.")
    private String email;

    @NotBlank(message = "Password is required.")
    @Size(min = 5, message = "Password must be at least 5 characters long.")
    private String password;
}
