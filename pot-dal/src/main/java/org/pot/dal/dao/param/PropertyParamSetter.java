package org.pot.dal.dao.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface PropertyParamSetter<T> {
    void setParam(PreparedStatement statement, T entity) throws SQLException;
}
