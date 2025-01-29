package com.example.symptomchecker.controller;

import com.example.symptomchecker.controller.requests.LoginRequest;
import com.example.symptomchecker.controller.requests.RegisterRequest;
import com.example.symptomchecker.model.User;
import com.example.symptomchecker.service.UserService;
import com.example.symptomchecker.service.exception.ServiceException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
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
            // TODO: some logging
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        try {
            User user = userService.loginUser(request.email(), request.password());
            return ResponseEntity.ok(user.email());
        } catch (ServiceException exception) {
            // TODO: some logging
            return ResponseEntity.internalServerError().build();
        }
    }
}
