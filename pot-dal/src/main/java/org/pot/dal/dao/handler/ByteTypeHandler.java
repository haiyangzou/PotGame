package org.pot.dal.dao.handler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ByteTypeHandler implements TypeHandler<Byte> {

    @Override
    public Byte get(ResultSet rs, String columnName) throws SQLException {
        return rs.getByte(columnName);
    }

    @Override
    public void set(PreparedStatement statement, int index, Byte param) throws SQLException {
        statement.setByte(index, param);
    }
}
