package com.example.symptomchecker.controller;

import com.example.symptomchecker.controller.requests.LoginRequest;
import com.example.symptomchecker.controller.requests.RegisterRequest;
import com.example.symptomchecker.model.User;
import com.example.symptomchecker.service.UserService;
import com.example.symptomchecker.service.exception.ServiceException;
import com.example.symptomchecker.util.LogUtil;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private static final Logger log = LogUtil.getLogger(AuthenticationController.class);

    private final UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        log.info("Received request to register user: {}", request.email());
        User user = new User(
            request.email(),
            request.password(),
            request.age(),
            request.gender()
        );

        try {
            userService.registerUser(user);
            return ResponseEntity.ok("User registered successfully!");
        } catch (ServiceException exception) {
            log.error("User already exists: {}", request.email());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        log.info("Received request to login user: {}", request.email());
        try {
            User user = userService.loginUser(request.email(), request.password());
            return ResponseEntity.ok(user.email());
        } catch (ServiceException exception) {
            log.error("Invalid email or password.");
            return ResponseEntity.internalServerError().build();
        }
    }
}
