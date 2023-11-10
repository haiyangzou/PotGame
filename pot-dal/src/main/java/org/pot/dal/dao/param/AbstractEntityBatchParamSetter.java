package org.pot.dal.dao.param;

import org.apache.commons.lang3.ArrayUtils;
import org.pot.dal.DbConstants;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class AbstractEntityBatchParamSetter<E> implements BatchParamSetter {
    protected final PropertyParamSetter<E> propertyParamSetter;

    public AbstractEntityBatchParamSetter(PropertyParamSetter<E> propertyParamSetter) {
        this.propertyParamSetter = propertyParamSetter;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public void setParam(PreparedStatement statement) throws SQLException {
        propertyParamSetter.setParam(statement, next());
    }

    @Override
    public int[] executeBatch(PreparedStatement statement) throws SQLException {
        int count = 0;
        int[] result = null;
        int batch = DbConstants.BATCH;
        while (hasNext()) {
            count++;
            setParam(statement);
            statement.addBatch();
            if (count >= batch) {
                count = 0;
                int[] updateCount = statement.executeBatch();
                result = result == null ? updateCount : ArrayUtils.addAll(result, updateCount);
            }
        }
        if (count > 0) {
            int[] updateCount = statement.executeBatch();
            result = result == null ? updateCount : ArrayUtils.addAll(result, updateCount);
        }
        return result != null ? result : ArrayUtils.EMPTY_INT_ARRAY;
    }

    protected abstract E next();
}
