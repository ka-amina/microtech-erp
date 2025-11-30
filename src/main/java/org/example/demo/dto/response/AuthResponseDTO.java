package org.example.demo.dto.response;

import lombok.*;
import org.example.demo.enums.UserRole;

@Data
public class AuthResponseDTO {
    private Long id;
    private String userName;
    private String email;
    private UserRole role;
    private String message;
}
