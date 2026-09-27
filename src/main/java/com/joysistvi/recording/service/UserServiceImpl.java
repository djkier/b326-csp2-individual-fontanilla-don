package com.joysistvi.recording.service;

import com.joysistvi.recording.model.User;
import com.joysistvi.recording.repository.UserRepo;

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

        return userRepo.findByUsername(username.trim());
    }

    @Override
    public List<User> searchUsers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            System.out.println("Search keyword cannot be empty.");
            return List.of();
        }

        return userRepo.searchUsers(keyword.trim());
    }
}
