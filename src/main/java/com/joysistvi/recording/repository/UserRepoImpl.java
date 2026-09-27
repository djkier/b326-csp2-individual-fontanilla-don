package com.joysistvi.recording.repository;

import com.joysistvi.recording.config.DBConnection;
import com.joysistvi.recording.model.Role;
import com.joysistvi.recording.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserRepoImpl implements UserRepo {
    private final DBConnection dbConnection;

    public UserRepoImpl(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT id, username, role FROM users";

        try (Connection conn = dbConnection.getConnection();
             Statement stmnt = conn.createStatement();
             ResultSet result = stmnt.executeQuery(query)) {

            while (result.next()) {
                users.add(mapUserWithoutPassword(result));
            }
        } catch (SQLException e) {
            System.err.println("Get All Users Error: " + e.getMessage());
        }

        return users;
    }

    @Override
    public List<User> getAllUsersForPasswordMigration() {
        List<User> users = new ArrayList<>();
        String query = "SELECT id, username, password, role FROM users";

        try (Connection conn = dbConnection.getConnection();
             Statement stmnt = conn.createStatement();
             ResultSet result = stmnt.executeQuery(query)) {

            while (result.next()) {
                users.add(mapUserWithPassword(result));
            }
        } catch (SQLException e) {
            System.err.println("Get Users For Password Migration Error: " + e.getMessage());
        }

        return users;
    }

    @Override
    public User getUserById(int id) {
        String query = "SELECT id, username, role FROM users WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {
            prep.setInt(1, id);

            try (ResultSet result = prep.executeQuery()) {
                if (result.next()) {
                    return mapUserWithoutPassword(result);
                }
            }
        } catch (SQLException e) {
            System.err.println("Read User By ID Error: " + e.getMessage());
        }

        return null;
    }

    @Override
    public User findByUsername(String username) {
        String query = "SELECT id, username, password, role FROM users WHERE username = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {
            prep.setString(1, username);

            try (ResultSet result = prep.executeQuery()) {
                if (result.next()) {
                    return mapUserWithPassword(result);
                }
            }
        } catch (SQLException e) {
            System.err.println("Find User By Username Error: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<User> searchUsers(String keyword) {
        List<User> users = new ArrayList<>();
        String query = "SELECT id, username, role FROM users WHERE username LIKE ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {
            prep.setString(1, "%" + keyword + "%");

            try (ResultSet result = prep.executeQuery()) {
                while (result.next()) {
                    users.add(mapUserWithoutPassword(result));
                }
            }
        } catch (SQLException e) {
            System.err.println("Search Users Error: " + e.getMessage());
        }

        return users;
    }

    @Override
    public boolean createUser(User user) {
        String query = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {
            prep.setString(1, user.getUsername());
            prep.setString(2, user.getPassword());
            prep.setString(3, user.getRole().name());
            return prep.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Create User Error: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean updatePassword(int userId, String passwordHash) {
        String query = "UPDATE users SET password = ? WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {
            prep.setString(1, passwordHash);
            prep.setInt(2, userId);
            return prep.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update User Password Error: " + e.getMessage());
        }

        return false;
    }

    private User mapUserWithPassword(ResultSet result) throws SQLException {
        return new User(
                result.getInt("id"),
                result.getString("username"),
                result.getString("password"),
                Role.valueOf(result.getString("role"))
        );
    }

    private User mapUserWithoutPassword(ResultSet result) throws SQLException {
        return new User(
                result.getInt("id"),
                result.getString("username"),
                null,
                Role.valueOf(result.getString("role"))
        );
    }
}
