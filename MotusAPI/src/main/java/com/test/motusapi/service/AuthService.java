package com.test.motusapi.service;

import com.test.motusapi.dto.LoginDto;
import com.test.motusapi.dto.LoginResponseDto;
import com.test.motusapi.dto.RegisterDto;
import com.test.motusapi.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public AuthService(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    public LoginResponseDto login(LoginDto loginDto) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginDto.getUsername());
        return new LoginResponseDto(jwtUtil.generateToken(userDetails.getUsername()), LocalDateTime.now().plusSeconds(36000).toString());
    }

    public String passwordEncoder(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean verify(String token) {
        return jwtUtil.validateToken(token);
    }

    public ResponseEntity<?> register(RegisterDto registerDto) {
        registerDto.setPassword(passwordEncoder(registerDto.getPassword()));
        if (!userService.createUser(registerDto)) {
            return ResponseEntity.badRequest().body("User already exists");
        }
        return ResponseEntity.ok("User registered successfully");
    }
}
