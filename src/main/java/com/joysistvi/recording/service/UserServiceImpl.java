package com.joysistvi.recording.service;

import com.joysistvi.recording.model.Role;
import com.joysistvi.recording.model.User;
import com.joysistvi.recording.repository.UserRepo;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;

    public UserServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepo.getAllUsers();
    }

    @Override
    public User getUserById(int id) {
        if (id <= 0) {
            System.out.println("Invalid user ID.");
            return null;
        }

        User user = userRepo.getUserById(id);
        if (user == null) {
            System.out.println("User not found.");
        }
        return user;
    }

    @Override
    public User findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }

        User user = userRepo.findByUsername(username.trim());
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }

    @Override
    public List<User> searchUsers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            System.out.println("Search keyword cannot be empty.");
            return List.of();
        }

        return userRepo.searchUsers(keyword.trim());
    }

    @Override
    public User authenticate(String username, String plainPassword) {
        if (username == null || username.trim().isEmpty() ||
                plainPassword == null || plainPassword.isBlank()) {
            return null;
        }

        User user = userRepo.findByUsername(username.trim());
        if (user == null || user.getPassword() == null) {
            return null;
        }

        try {
            if (!BCrypt.checkpw(plainPassword, user.getPassword())) {
                return null;
            }
        } catch (IllegalArgumentException e) {
            return null;
        }

        user.setPassword(null);
        return user;
    }

    @Override
    public boolean createUser(String username, String plainPassword, Role role) {
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Username is required.");
            return false;
        }
        if (!isValidPassword(plainPassword)) {
            return false;
        }
        if (role == null) {
            System.out.println("User role is required.");
            return false;
        }

        String normalizedUsername = username.trim();
        if (userRepo.findByUsername(normalizedUsername) != null) {
            System.out.println("Username already exists.");
            return false;
        }

        String passwordHash = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        return userRepo.createUser(new User(0, normalizedUsername, passwordHash, role));
    }

    @Override
    public boolean resetPassword(int userId, String plainPassword) {
        if (userId <= 0) {
            System.out.println("Invalid user ID.");
            return false;
        }
        if (!isValidPassword(plainPassword)) {
            return false;
        }
        if (userRepo.getUserById(userId) == null) {
            System.out.println("User not found.");
            return false;
        }

        String passwordHash = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        return userRepo.updatePassword(userId, passwordHash);
    }

    private boolean isValidPassword(String password) {
        if (password == null || password.isBlank()) {
            System.out.println("Password is required.");
            return false;
        }
        return true;
    }
}
