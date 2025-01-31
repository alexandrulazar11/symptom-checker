package com.example.symptomchecker.service;

import com.example.symptomchecker.model.User;
import com.example.symptomchecker.repository.UserRepository;
import com.example.symptomchecker.service.exception.ServiceException;
import com.example.symptomchecker.util.LogUtil;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private static final Logger log = LogUtil.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void registerUser(User user) {
        User existingUser = userRepository.getUser(user.getEmail());

        if (existingUser != null) {
            throw new ServiceException("User already exists with email: " + user.getEmail());
        }

        userRepository.saveUser(user);
    }

    public User loginUser(String email, String password) {
        User user = userRepository.getUser(email);
        if (user == null || !user.getPassword().equals(password)) {
            throw new ServiceException("Invalid email or password.");
        }

        return user;
    }
}
