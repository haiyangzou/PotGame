package org.pot.dal.dao.handler;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BigDecimalTypeHandler implements TypeHandler<BigDecimal> {

    @Override
    public BigDecimal get(ResultSet rs, String columnName) throws SQLException {
        return rs.getBigDecimal(columnName);
    }

    @Override
    public void set(PreparedStatement statement, int index, BigDecimal param) throws SQLException {
        statement.setBigDecimal(index, param);
    }
}
