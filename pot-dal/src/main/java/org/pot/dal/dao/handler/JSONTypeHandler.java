package org.pot.dal.dao.handler;


import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.commons.lang3.StringUtils;
import org.pot.common.util.JsonUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JSONTypeHandler<T> implements TypeHandler<T> {
    private final JavaType type;

    public JSONTypeHandler(Type type) {
        this(TypeFactory.defaultInstance().constructType(type));
    }

    public JSONTypeHandler(JavaType type) {
        this.type = type;
    }

    @Override
    public T get(ResultSet rs, String columnName) throws SQLException {
        String text = rs.getString(columnName);
        if (StringUtils.isBlank(text)) {
            return null;
        }
        try {
            return JsonUtil.parseJackJson(text, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void set(PreparedStatement statement, int index, Object param) throws SQLException {
        if (param == null) {
            statement.setObject(index, null);
            return;
        }
        String text = JsonUtil.toJson(param);
        statement.setString(index, text);
    }
}
