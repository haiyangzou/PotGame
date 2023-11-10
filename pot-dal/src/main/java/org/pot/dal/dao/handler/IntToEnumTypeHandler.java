package org.pot.dal.dao.handler;

import lombok.extern.slf4j.Slf4j;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class IntToEnumTypeHandler<T extends Enum<T>> implements TypeHandler<T> {
    private final Class<T> enumClass;
    private T[] values;

    public IntToEnumTypeHandler(Class<T> enumType) {
        this.enumClass = enumType;
        try {
            this.values = (T[]) enumClass.getDeclaredMethod("values").invoke(null);
        } catch (Throwable e) {
            log.error("failed to get Enum values of {}", enumType, e);
        }
    }

    @Override
    public T get(ResultSet rs, String columnName) throws SQLException {
        int ordinal = rs.getInt(columnName);
        if (ordinal >= 0 && ordinal < values.length) {
            return values[ordinal];
        }
        return null;
    }

    @Override
    public void set(PreparedStatement statement, int index, T param) throws SQLException {
        if (param == null) {
            statement.setObject(index, null);
            return;
        }
        statement.setInt(index, param.ordinal());
    }
}
