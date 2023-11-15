package org.pot.dal.dao.function;

import org.pot.dal.db.EntityParser;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EntityListParserFunction<T> implements QueryFunction<List<T>> {
    private final EntityParser<T> parser;

    public EntityListParserFunction(EntityParser<T> parser) {
        this.parser = parser;
    }

    @Override
    public List<T> apply(PreparedStatement stmt) throws SQLException {
        try (ResultSet resultSet = stmt.executeQuery()) {
            List<T> list = new ArrayList<>();
            while (resultSet.next()) {
                list.add(parser.parse(resultSet));
            }
            return list;
        }
    }
}
