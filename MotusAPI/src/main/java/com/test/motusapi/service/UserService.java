package com.test.motusapi.service;

import com.test.motusapi.dto.RegisterDto;
import com.test.motusapi.model.User;
import com.test.motusapi.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean createUser(RegisterDto registerDto) {
        if (userRepository.findByUsername(registerDto.getUsername()).isPresent() ||
                userRepository.findByEmail(registerDto.getEmail()).isPresent()) {
            return false; // User already exists
        }
        userRepository.save(new User(registerDto.getUsername(), registerDto.getPassword(), registerDto.getEmail()));
        return true;
    }
}
