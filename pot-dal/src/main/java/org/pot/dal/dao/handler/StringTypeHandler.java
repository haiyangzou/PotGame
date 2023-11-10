package org.pot.dal.dao.handler;


import com.mysql.cj.util.StringUtils;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StringTypeHandler implements TypeHandler<Object> {

    @Override
    public Object get(ResultSet rs, String columnName) throws SQLException {
        return rs.getString(columnName);
    }

    @Override
    public void set(PreparedStatement statement, int index, Object param) throws SQLException {
        if (param == null) {
            statement.setObject(index, null);
            return;
        }
        if (param instanceof String) {
            statement.setString(index, (String) param);
        } else if (param instanceof BigDecimal) {
            statement.setString(index, (StringUtils.fixDecimalExponent(((BigDecimal) param).toPlainString())));
        } else {
            statement.setString(index, param.toString());
        }
    }
}
