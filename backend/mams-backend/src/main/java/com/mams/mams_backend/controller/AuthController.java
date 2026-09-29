package com.mams.mams_backend.controller;

import com.mams.mams_backend.dto.AuthResponse;
import com.mams.mams_backend.dto.LoginRequest;
import com.mams.mams_backend.entity.User;
import com.mams.mams_backend.repository.UserRepository;
import com.mams.mams_backend.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password()));

        User u = userRepository.findByUsername(req.username()).orElseThrow();
        String role = u.getRole().getName().name();
        String token = jwtUtil.generateToken(u.getUsername(), role);

        return ResponseEntity.ok(new AuthResponse(token, u.getUsername(), role,
                u.getBase() != null ? u.getBase().getId() : null,
                u.getBase() != null ? u.getBase().getName() : null));
    }
}