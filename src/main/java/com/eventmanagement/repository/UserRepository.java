package com.eventmanagement.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import com.eventmanagement.model.Users;
import com.eventmanagement.utils.SQLQueries;
import com.eventmanagement.utils.dbconfig.DataBaseConnection;

public class UserRepository {

    private static final Connection DB = DataBaseConnection.getConnection();

    public boolean save(Users user) throws SQLException {
        try (PreparedStatement stmt = DB.prepareStatement(SQLQueries.CREATE_USER)) {
            stmt.setString(1, user.getUser_name());
            stmt.setString(2, user.getUser_password()); 
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(UUID userId) throws SQLException {
        try (PreparedStatement stmt = DB.prepareStatement(SQLQueries.DELETE_USER)) {
            stmt.setObject(1, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    public Users findById(UUID userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (PreparedStatement stmt = DB.prepareStatement(sql)) {
            stmt.setObject(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Users user = new Users();
                    user.setUser_id((UUID) rs.getObject("user_id"));
                    user.setUser_name(rs.getString("user_name"));
                    user.setUser_password(rs.getString("user_password"));
                    return user;
                }
            }
        }
        return null;
    }
}
