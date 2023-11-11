package org.pot.dal.dao.function;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface QueryFunction<T> {
    T apply(PreparedStatement stmt) throws SQLException;
}
