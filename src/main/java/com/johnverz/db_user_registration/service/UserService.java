package com.johnverz.db_user_registration.service;

import com.johnverz.db_user_registration.entity.User;
import com.johnverz.db_user_registration.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MySqlUserService mySqlUserService;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }


    public User registerUser(User user) throws Exception {
        // Encode password for application use
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Generate MySQL credentials
        String mysqlUsername = "user_" + user.getUsername().toLowerCase();
        String mysqlPassword = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        
        // Save user first
        User savedUser = userRepository.save(user);

        // Create MySQL user
        try {
            mySqlUserService.createMySqlUser(mysqlUsername, mysqlPassword);
        } catch (Exception e) {
            // Rollback user creation if MySQL user creation fails
            userRepository.delete(savedUser);
            throw new Exception("Failed to create MySQL user: " + e.getMessage());
        }

        return savedUser;
    }
}