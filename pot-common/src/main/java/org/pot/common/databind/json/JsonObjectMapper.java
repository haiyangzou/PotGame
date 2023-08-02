package org.pot.common.databind.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.pot.common.exception.UnsupportedTypeException;
import org.pot.common.util.JsonUtil;
import org.reflections.Reflections;

public class JsonObjectMapper {
    private static volatile boolean error = false;

    public String serialize(final Object object) {
        if (object instanceof JsonObject) {
            try {
                return JsonUtil.toJackSon(object);
            } catch (JsonProcessingException exception) {
                throw new RuntimeException(exception);
            }
        }
        throw new UnsupportedTypeException(object.getClass());
    }

    public void ensureJsonObjectsValid(String... databindPackageNames) {

    }

    private synchronized void ensureJsonObjectsValid(final Reflections reflections) {
    }

}
