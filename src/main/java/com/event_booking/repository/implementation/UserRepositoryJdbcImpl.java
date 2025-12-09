package com.event_booking.repository.implementation;

import java.sql.Connection;

import com.event_booking.model.Users;
import com.event_booking.persistence.EntityMapper;
import com.event_booking.repository.JdbcRepository;

public class UserRepositoryJdbcImpl extends JdbcRepository<Users, String> {

	protected UserRepositoryJdbcImpl(Connection connection, EntityMapper<Users> mapper) {
        super(connection, mapper);
    }

    @Override
	protected String getTableName() {
		return "users";
	}

	@Override
	protected String getPrimaryKeyColumn() {
		return "user_id";
	}

}
