package com.ivocorrea.investmanager.controller;

import com.ivocorrea.investmanager.dto.user.CreateUserDto;
import com.ivocorrea.investmanager.dto.login.LoginRequestDTO;
import com.ivocorrea.investmanager.dto.login.LoginResponseDTO;
import com.ivocorrea.investmanager.dto.RefreshRequestDTO;
import com.ivocorrea.investmanager.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponseDTO> registerUser(@RequestBody CreateUserDto userDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(userDto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginAuth(@RequestBody LoginRequestDTO requestDTO) {
        return ResponseEntity.ok(authService.login(requestDTO));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refreshAuth(@RequestBody RefreshRequestDTO requestDTO){
        return ResponseEntity.ok(authService.authRefresh(requestDTO.refreshToken()));
    }
}
