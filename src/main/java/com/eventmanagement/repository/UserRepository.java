package com.eventmanagement.repository;

import com.eventmanagement.model.Users;

public class UserRepository {

    private final java.sql.Connection connection;

    public UserRepository(java.sql.Connection connection) {
        this.connection = connection;
    }

    public Users findById(String user_id) {
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        try {
            String sql = "SELECT user_id, user_name, user_password, user_role FROM users WHERE user_id = ?";
            ps = connection.prepareStatement(sql);
            ps.setObject(1, java.util.UUID.fromString(user_id));
            rs = ps.executeQuery();
            if (rs.next()) {
                Users u = new Users();
                u.setUser_id(rs.getObject("user_id").toString());
                u.setUser_name(rs.getString("user_name"));
                u.setUser_password(rs.getString("user_password"));
                u.setUser_role(rs.getString("user_role"));
                return u;
            } else {
                return null;
            }
        } catch (java.sql.SQLException ex) {
            throw new RuntimeException("Error querying user by id", ex);
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
        }
    }

    public Users findByUsername(String user_name) {
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        try {
            String sql = "SELECT user_id, user_name, user_password, user_role FROM users WHERE user_name = ?";
            ps = connection.prepareStatement(sql);
            ps.setString(1, user_name);
            rs = ps.executeQuery();
            if (rs.next()) {
                Users u = new Users();
                u.setUser_id(rs.getObject("user_id").toString());
                u.setUser_name(rs.getString("user_name"));
                u.setUser_password(rs.getString("user_password"));
                u.setUser_role(rs.getString("user_role"));
                return u;
            } else {
                return null;
            }
        } catch (java.sql.SQLException ex) {
            throw new RuntimeException("Error querying user by username", ex);
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
        }
    }

    /**
     * Create a new user. Returns generated user_id (RETURNING).
     * Expects user_password to already be hashed.
     */
    public String createUser(String user_name, String hashedPassword, String user_role) {
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        try {
            String sql = "INSERT INTO users (user_name, user_password, user_role) VALUES (?, ?, ?) RETURNING user_id";
            ps = connection.prepareStatement(sql);
            ps.setString(1, user_name);
            ps.setString(2, hashedPassword);
            ps.setString(3, user_role);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getObject("user_id").toString();
            } else {
                throw new RuntimeException("Failed to create user");
            }
        } catch (java.sql.SQLException ex) {
            // handle unique username violation more gracefully if you want
            throw new RuntimeException("Error creating user", ex);
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
        }
    }

    /**
     * Authenticate user by username + hashed password.
     * Returns Users on success, null if wrong credentials.
     */
    public Users authenticateUser(String user_name, String hashedPassword) {
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        try {
            String sql = "SELECT user_id, user_name, user_password, user_role FROM users WHERE user_name = ? AND user_password = ?";
            ps = connection.prepareStatement(sql);
            ps.setString(1, user_name);
            ps.setString(2, hashedPassword);
            rs = ps.executeQuery();
            if (rs.next()) {
                Users u = new Users();
                u.setUser_id(rs.getObject("user_id").toString());
                u.setUser_name(rs.getString("user_name"));
                u.setUser_password(rs.getString("user_password"));
                u.setUser_role(rs.getString("user_role"));
                return u;
            } else {
                return null;
            }
        } catch (java.sql.SQLException ex) {
            throw new RuntimeException("Error authenticating user", ex);
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
        }
    }

    private void closeQuietly(java.sql.ResultSet rs) {
        if (rs != null) {
            try { rs.close(); } catch (Exception ignored) {}
        }
    }

    private void closeQuietly(java.sql.Statement st) {
        if (st != null) {
            try { st.close(); } catch (Exception ignored) {}
        }
    }
}
