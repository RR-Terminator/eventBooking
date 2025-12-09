package com.event_booking.persistence;

import java.util.Map;

public interface PersistableEntity<ID> {

    String getTableName();

    Map<String, Object> getColumnValues();

    String getPrimaryKeyColumn();

    ID getPrimaryKeyValue();

    void setPrimaryKeyValue(ID id);
}
