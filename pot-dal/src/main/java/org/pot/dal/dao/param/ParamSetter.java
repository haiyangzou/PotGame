package org.pot.dal.dao.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface ParamSetter {
    void setParam(PreparedStatement statement) throws SQLException;
}
