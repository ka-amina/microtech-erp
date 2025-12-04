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
                .role(req.getRole())
                .build();
    }

    public AuthResponseDTO toAuthResponse(User user, String message) {
        AuthResponseDTO res= new AuthResponseDTO();
        res.setId(user.getId());
        res.setUserName(user.getUserName());
        res.setRole(user.getRole());
        res.setMessage(message);
        return res;
    }
}

