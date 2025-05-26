package com.test.motusapi.controller;

import com.test.motusapi.dto.LoginDto;
import com.test.motusapi.dto.LoginResponseDto;
import com.test.motusapi.dto.RegisterDto;
import com.test.motusapi.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginDto loginDto) {
        return authService.login(loginDto);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto registerDto) {
        return authService.register(registerDto);
    }

    @PostMapping("/verify")
    public ResponseEntity<Boolean> verify(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.ok(false);
        }
        try {
            String jwt = token.substring(7);
            return ResponseEntity.ok(authService.verify(jwt));
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }
}
