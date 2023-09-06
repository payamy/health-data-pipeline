package com.payamy.health.server.controller;

import com.payamy.health.server.dto.LoginDto;
import com.payamy.health.server.dto.LoginResponse;
import com.payamy.health.server.dto.SignUpDto;
import com.payamy.health.server.entity.AccessToken;
import com.payamy.health.server.entity.User;
import com.payamy.health.server.repo.UserRepository;
import com.payamy.health.server.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login( @RequestBody LoginDto loginDto ) {
        User user = userRepository.findByUsername(loginDto.getUsername());

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            return new ResponseEntity<>("Credentials are not valid", HttpStatus.BAD_REQUEST);
        }

        Long userId = user.getId();
        AccessToken accessToken = authService.getAccessToken(userId);

        String token = "";
        if (accessToken == null) {
            token = UUID.randomUUID().toString();
            authService.putAccessToken(token, userId);
        } else {
            token = accessToken.getToken();
        }

        return new ResponseEntity<>(
                LoginResponse.builder()
                        .token(token)
                        .build(),
                HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpDto signUpDto) {

        // add check for username exists in a DB
        if (userRepository.existsByUsername(signUpDto.getUsername())) {
            return new ResponseEntity<>("Username is already taken!", HttpStatus.BAD_REQUEST);
        }

        // create user object
        User user = User.builder()
                            .username(signUpDto.getUsername())
                            .password(passwordEncoder.encode(signUpDto.getPassword()))
                            .bloodType(signUpDto.getBloodType())
                            .eyeColor(signUpDto.getEyeColor())
                            .name(signUpDto.getName())
                            .createdAt(Calendar.getInstance().getTime())
                            .build();

        userRepository.save(user);

        return new ResponseEntity<>("User registered successfully", HttpStatus.OK);
    }
}
