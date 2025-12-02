package org.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.example.demo.dto.request.LoginRequestDTO;
import org.example.demo.dto.request.RegisterRequestDTO;
import org.example.demo.dto.response.AuthResponseDTO;
import org.example.demo.exception.DuplicateResourceException;
import org.example.demo.exception.ResourceNotFoundException;
import org.example.demo.exception.UnauthorizedException;
import org.example.demo.mappers.UserMapper;
import org.example.demo.model.User;
import org.example.demo.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public AuthResponseDTO register(RegisterRequestDTO req) {
        if (userRepository.existsByUserName(req.getUserName())) {
            throw new DuplicateResourceException("user already exists");
        }
        User user = userMapper.toEntity(req);
        user.setPassword(hashPassword(req.getPassword()));

        User savedUser = userRepository.save(user);
        return userMapper.toAuthResponse(savedUser, "user registred successfully");
    }

    public  AuthResponseDTO login(LoginRequestDTO req) {
        User user=  userRepository.findByUserName(req.getUserName()).orElseThrow(() -> new ResourceNotFoundException(" user not found"));
        if (!checkPaswword(req.getPassword(), user.getPassword())) {
            throw new UnauthorizedException(" invallid credentials");
        }
        return userMapper.toAuthResponse(user, "user login successfully");
    }

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean checkPaswword(String password, String checkedPassword) {
        return BCrypt.checkpw(password, checkedPassword);
    }

    public AuthResponseDTO getUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user not found"));
        return userMapper.toAuthResponse(user, "user details fetched successfully");
    }


}
