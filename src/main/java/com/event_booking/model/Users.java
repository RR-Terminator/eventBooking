package com.event_booking.model;

import java.util.LinkedHashMap;
import java.util.Map;

import com.event_booking.persistence.PersistableEntity;

public class Users implements PersistableEntity<String> {

    private String user_id;
    private String user_name;
    private String user_password;
    private String user_role;

    public Users() {
    }

    public Users(String user_id, String user_name, String user_password, String user_role) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_password = user_password;
        this.user_role = user_role;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public String getUser_role() {
        return user_role;
    }

    public void setUser_role(String user_role) {
        this.user_role = user_role;
    }

    @Override
    public String getTableName() {
        return "users";
    }

    @Override
    public Map<String, Object> getColumnValues() {
        Map<String, Object> map = new LinkedHashMap<>();

        map.put("user_id", user_id);
        map.put("user_name", user_name);
        map.put("user_password", user_password);
        map.put("user_role", user_role);

        return map;
    }

    @Override
    public String getPrimaryKeyColumn() {

        return "user_id";
    }

    @Override
    public String getPrimaryKeyValue() {
        return this.user_id;
    }

    @Override
    public void setPrimaryKeyValue(String id) {
        this.user_id = id;
    }
}
