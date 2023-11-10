package org.pot.dal.async;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pot.dal.DbConstants;
import org.pot.dal.dao.SessionMapper;
import org.pot.dal.dao.SqlSession;

import java.util.function.Consumer;

@Slf4j
public class AsyncWithoutResultDbTask<M extends SessionMapper> implements IAsyncDbTask {
    private final String caller;
    private final SqlSession sqlSession;
    private final Class<M> mapperClass;
    private final Consumer<M> mapperMethod;
    @Getter
    private final AsyncWithoutResultDbTaskCallback asyncWithoutResultDbTaskCallback;

    @Getter
    private volatile Throwable throwable;

    public AsyncWithoutResultDbTask(String caller, SqlSession sqlSession, Class<M> mapperClass,
                                    Consumer<M> mapperMethod, AsyncWithoutResultDbTaskCallback asyncWithoutResultDbTaskCallback) {
        this.caller = caller;
        this.sqlSession = sqlSession;
        this.mapperClass = mapperClass;
        this.mapperMethod = mapperMethod;
        this.asyncWithoutResultDbTaskCallback = asyncWithoutResultDbTaskCallback;
    }

    @Override
    public long getId() {
        return sqlSession.getId();
    }

    @Override
    public void execute() {
        long time = System.currentTimeMillis();
        try {
            M mapper = sqlSession.getMapper(mapperClass);
            if (mapperMethod != null) {
                mapperMethod.accept(mapper);
            }
        } catch (Throwable ex) {
            throwable = ex;
            log.error("DBTask Error:id={},mapper={},caller={}", getId(), mapperClass.getName(), caller, ex);
        }
        try {
            if (asyncWithoutResultDbTaskCallback != null) {
                asyncWithoutResultDbTaskCallback.setAsyncWithoutResultDbTask(this);
                asyncWithoutResultDbTaskCallback.callback();
            }
        } catch (Throwable ex) {
            log.error("DBTask Error:id={},mapper={},caller={}", getId(), mapperClass.getName(), caller, ex);
        }
        long elapsed = System.currentTimeMillis() - time;
        if (elapsed > DbConstants.SLOW_MS) {
            IAsyncDbTask.elapsedTimeMonitor.recordElapsedTime(caller, elapsed);
        }
    }
}
