package org.pot.dal.dao.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ArrayParamSetter implements ParamSetter {
    private final Object[] params;

    public ArrayParamSetter(Object... params) {
        this.params = params;
    }

    @Override
    public void setParam(PreparedStatement statement) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            statement.setObject(i + 1, params[i]);
        }
    }
}
