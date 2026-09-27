package com.joysistvi.recording.controller;

import com.joysistvi.recording.model.User;
import com.joysistvi.recording.service.UserService;

import java.util.List;

public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public List<User> handleViewAllUsers() {
        return userService.getAllUsers();
    }

    public User handleGetUserById(int id) {
        return userService.getUserById(id);
    }

    public User handleFindByUsername(String username) {
        return userService.findByUsername(username);
    }

    public List<User> handleSearchUsers(String keyword) {
        return userService.searchUsers(keyword);
    }
}
