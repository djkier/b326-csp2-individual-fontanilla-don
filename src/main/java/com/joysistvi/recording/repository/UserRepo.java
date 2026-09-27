package com.joysistvi.recording.repository;

import com.joysistvi.recording.model.User;

import java.util.List;

public interface UserRepo {
    List<User> getAllUsers();
    User getUserById(int id);
    User findByUsername(String username);
    List<User> searchUsers(String keyword);
}
