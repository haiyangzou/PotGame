package org.pot.dal.dao.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ListParamSetter implements ParamSetter {
    private final List params;

    public ListParamSetter(List params) {
        this.params = params;
    }

    @Override
    public void setParam(PreparedStatement statement) throws SQLException {
        int size = params.size();
        for (int i = 0; i < size; i++) {
            statement.setObject(i + 1, params.get(i));
        }
    }
}
