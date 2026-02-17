package com.library.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

/**
 * Login Request DTO
 */
@Data
public class LoginRequest {
    @NotBlank(message = "Username is required")
    private String username;
    
    @NotBlank(message = "Password is required")
    private String password;
}
