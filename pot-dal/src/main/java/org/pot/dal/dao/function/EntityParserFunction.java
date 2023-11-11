package org.pot.dal.dao.function;

import lombok.extern.slf4j.Slf4j;
import org.pot.dal.db.EntityParser;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class EntityParserFunction<T> implements QueryFunction<T> {
    private final EntityParser<T> parser;

    public EntityParserFunction(EntityParser<T> parser) {
        this.parser = parser;
    }

    @Override
    public T apply(PreparedStatement stmt) throws SQLException {
        try (ResultSet resultSet = stmt.executeQuery()) {
            if (resultSet.next()) {
                try {
                    return parser.parse(resultSet);
                } catch (Throwable ex) {
                    log.error("failed to parse Result");
                }
            }
        }
        return null;
    }
}
