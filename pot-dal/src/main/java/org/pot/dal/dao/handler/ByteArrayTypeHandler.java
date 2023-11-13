package org.pot.dal.dao.handler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ByteArrayTypeHandler implements TypeHandler<byte[]> {

    @Override
    public byte[] get(ResultSet rs, String columnName) throws SQLException {
        return rs.getBytes(columnName);
    }

    @Override
    public void set(PreparedStatement statement, int index, byte[] param) throws SQLException {
        statement.setBytes(index, param);
    }
}
