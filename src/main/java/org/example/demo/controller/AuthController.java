package org.example.demo.controller;

import lombok.RequiredArgsConstructor;
import org.example.demo.dto.request.RegisterRequestDTO;
import org.example.demo.dto.response.AuthResponseDTO;
import org.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/auth")
public class AuthController {
    private final UserService userService;

    @PostMapping("register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterRequestDTO req){
        AuthResponseDTO res= userService.register(req);
        return ResponseEntity.ok(res);
    }
}
