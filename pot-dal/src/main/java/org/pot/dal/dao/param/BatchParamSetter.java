package org.pot.dal.dao.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface BatchParamSetter {
    boolean hasNext();

    void setParam(PreparedStatement statement) throws SQLException;

    int[] executeBatch(PreparedStatement statement) throws SQLException;
}
