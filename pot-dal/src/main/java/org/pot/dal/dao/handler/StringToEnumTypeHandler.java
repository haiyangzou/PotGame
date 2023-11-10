package org.pot.dal.dao.handler;

import org.apache.commons.lang3.StringUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StringToEnumTypeHandler<T extends Enum<T>> implements TypeHandler<T> {
    private final Class<T> enumType;
    private final T defaultValue;

    public StringToEnumTypeHandler(Class<T> enumType, T defaultValue) {
        this.enumType = enumType;
        this.defaultValue = defaultValue;
    }

    @Override
    public T get(ResultSet rs, String columnName) throws SQLException {
        String text = rs.getString(columnName);
        if (StringUtils.isBlank(text)) {
            return defaultValue;
        }
        return Enum.valueOf(enumType, text);
    }

    @Override
    public void set(PreparedStatement statement, int index, T param) throws SQLException {
        if (param == null) {
            statement.setString(index, null);
            return;
        }
        statement.setString(index, param.name());
    }
}
