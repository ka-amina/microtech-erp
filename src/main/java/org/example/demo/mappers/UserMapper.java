package org.example.demo.mappers;

import org.example.demo.dto.request.RegisterRequestDTO;
import org.example.demo.dto.response.AuthResponseDTO;
import org.example.demo.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(RegisterRequestDTO req) {
        return User.builder()
                .userName(req.getUserName())
                .email(req.getEmail())
                .role(req.getRole())
                .build();
    }

    public AuthResponseDTO toAuthResponse(User user, String message) {
        AuthResponseDTO res= new AuthResponseDTO();
        res.setId(user.getId());
        res.setUserName(user.getUserName());
        res.setEmail(user.getEmail());
        res.setEmail(user.getEmail());
        res.setRole(user.getRole());
        return res;
    }
}
