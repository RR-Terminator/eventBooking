package com.event_booking.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.event_booking.persistence.EntityMapper;
import com.event_booking.persistence.PersistableEntity;

public abstract class JdbcRepository<T extends PersistableEntity<ID>, ID>
        implements BaseRepository<T, ID> {

    protected final Connection connection;
    protected final EntityMapper<T> mapper;

    protected JdbcRepository(Connection connection, EntityMapper<T> mapper) {
        this.connection = connection;
        this.mapper = mapper;
    }

    protected abstract String getTableName();

    protected abstract String getPrimaryKeyColumn();

    // ------------ SAVE ------------
    @Override
    public ID save(T entity) {
        Map<String, Object> cols = entity.getColumnValues();

        String columns = String.join(", ", cols.keySet());
        String placeholders = cols.keySet().stream()
                .map(c -> "?")
                .collect(Collectors.joining(", "));

        String sql = "INSERT INTO " + getTableName() +
                " (" + columns + ") VALUES (" + placeholders + ")";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            int i = 1;
            for (Object value : cols.values()) {
                ps.setObject(i++, convert(value));
            }

            ps.executeUpdate();

            // Set generated ID (optional)
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    @SuppressWarnings("unchecked")
                    ID id = (ID) rs.getObject(1);
                    entity.setPrimaryKeyValue(id);
                }
            }

            return entity.getPrimaryKeyValue();

        } catch (SQLException e) {
            throw new RuntimeException("Error saving entity", e);
        }
    }

    // ------------ UPDATE ------------
    @Override
    public T update(T entity) {
        Map<String, Object> cols = new LinkedHashMap<>(entity.getColumnValues());
        ID id = entity.getPrimaryKeyValue();

        cols.remove(getPrimaryKeyColumn());

        String setPart = cols.keySet().stream()
                .map(c -> c + " = ?")
                .collect(Collectors.joining(", "));

        String sql = "UPDATE " + getTableName() +
                " SET " + setPart +
                " WHERE " + getPrimaryKeyColumn() + " = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            int i = 1;
            for (Object v : cols.values()) {
                ps.setObject(i++, convert(v));
            }
            ps.setObject(i, id);

            ps.executeUpdate();
            return entity;

        } catch (SQLException e) {
            throw new RuntimeException("Error updating entity", e);
        }
    }

    // ------------ FIND BY ID ------------
    @Override
    public Optional<T> findById(ID id) {
        String sql = "SELECT * FROM " + getTableName() +
                " WHERE " + getPrimaryKeyColumn() + " = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapper.map(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching by ID", e);
        }
    }

    // ------------ FIND ALL ------------
    @Override
    public List<T> findAll() {
        String sql = "SELECT * FROM " + getTableName();

        try (PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            List<T> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapper.map(rs));
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all", e);
        }
    }

    // ------------ DELETE ------------
    @Override
    public boolean delete(ID id) {
        String sql = "DELETE FROM " + getTableName() +
                " WHERE " + getPrimaryKeyColumn() + " = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting entity", e);
        }
    }

    // ------------ TYPE CONVERSION ------------
    protected Object convert(Object value) {
        if (value instanceof java.time.LocalDateTime dt) {
            return Timestamp.valueOf(dt);
        }
        return value;
    }
}
