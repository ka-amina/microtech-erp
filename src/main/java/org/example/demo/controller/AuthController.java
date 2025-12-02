package org.example.demo.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.demo.dto.request.LoginRequestDTO;
import org.example.demo.dto.request.RegisterRequestDTO;
import org.example.demo.dto.response.AuthResponseDTO;
import org.example.demo.exception.UnauthorizedException;
import org.example.demo.model.User;
import org.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/auth")
public class AuthController {
    private final UserService userService;

    @PostMapping("register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterRequestDTO req) {
        AuthResponseDTO res = userService.register(req);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO req, HttpSession session) {
        AuthResponseDTO res = userService.login(req);
        session.setAttribute("userId", res.getId());
        session.setAttribute("userRole", res.getRole());
        System.out.println(session.getAttribute("userRole"));
        return ResponseEntity.ok(res);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("logged out successfully");
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponseDTO> getCurrentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if(userId == null) {
            throw new UnauthorizedException("You are not logged in");
        }
        AuthResponseDTO res = userService.getUserById(userId);
        return ResponseEntity.ok(res);
    }
}
