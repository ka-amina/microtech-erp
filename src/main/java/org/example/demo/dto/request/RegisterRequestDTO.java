package org.example.demo.dto.request;

import lombok.*;
import org.example.demo.enums.UserRole;

@Data
public class RegisterRequestDTO {
    private String userName;
    private String email;
    private String password ;
    private UserRole role;
}
