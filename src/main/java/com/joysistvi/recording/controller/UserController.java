package com.joysistvi.recording.controller;

import com.joysistvi.recording.model.Role;
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

    public User handleAuthenticate(String username, String plainPassword) {
        return userService.authenticate(username, plainPassword);
    }

    public boolean handleCreateUser(String username, String plainPassword, Role role) {
        return userService.createUser(username, plainPassword, role);
    }

    public boolean handleResetPassword(int userId, String plainPassword) {
        return userService.resetPassword(userId, plainPassword);
    }
}
