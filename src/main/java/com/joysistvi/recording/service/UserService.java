package com.joysistvi.recording.service;

import com.joysistvi.recording.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(int id);
    User findByUsername(String username);
    List<User> searchUsers(String keyword);
}
