package com.studentinfo.dao;

import com.studentinfo.database.DatabaseConnection;
import com.studentinfo.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private final DatabaseConnection dbConnection;

    public UserDAO(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public void addUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, role, email, phone) " +
                    "VALUES (?, ?, ?, ?, ?)";
        
        Connection conn = dbConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        try {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getPhone());
            pstmt.executeUpdate();
        } finally {
            pstmt.close();
        }
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        Connection conn = dbConnection.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        try {
            while (rs.next()) {
                User user = new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role"),
                    rs.getString("email"),
                    rs.getString("phone")
                );
                users.add(user);
            }
        } finally {
            rs.close();
            stmt.close();
        }
        return users;
    }

    public User getUserById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        
        Connection conn = dbConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        try {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            try {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("email"),
                        rs.getString("phone")
                    );
                }
            } finally {
                rs.close();
            }
        } finally {
            pstmt.close();
        }
        return null;
    }

    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        Connection conn = dbConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        try {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            try {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("email"),
                        rs.getString("phone")
                    );
                }
            } finally {
                rs.close();
            }
        } finally {
            pstmt.close();
        }
        return null;
    }

    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET username = ?, password = ?, role = ?, " +
                    "email = ?, phone = ? WHERE id = ?";
        
        Connection conn = dbConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        try {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getPhone());
            pstmt.setInt(6, user.getId());
            pstmt.executeUpdate();
        } finally {
            pstmt.close();
        }
    }

    public void deleteUser(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        
        Connection conn = dbConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        try {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } finally {
            pstmt.close();
        }
    }

    public boolean validateUser(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        
        Connection conn = dbConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        try {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            try {
                return rs.next();
            } finally {
                rs.close();
            }
        } finally {
            pstmt.close();
        }
    }
} 