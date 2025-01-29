package com.example.symptomchecker.service;

import com.example.symptomchecker.model.User;
import com.example.symptomchecker.repository.UserRepository;
import com.example.symptomchecker.service.exception.ServiceException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void registerUser(User user) {
        User existingUser = userRepository.getUser(user.email());

        if (existingUser != null) {
            throw new ServiceException("User already exists with email: " + user.email());
        }

        userRepository.saveUser(user);
    }

    public User loginUser(String email, String password) {
        User user = userRepository.getUser(email);
        if (user == null || !user.password().equals(password)) {
            throw new ServiceException("Invalid email or password.");
        }

        return user;
    }
}
