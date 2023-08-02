package org.pot.common.databind.json;

public interface JsonObject {
    JsonObjectMapper JSON_OBJECT_MAPPER = new JsonObjectMapper();

    static void ensureJsonObjectsValid(String... databindPackageNames) {
        JSON_OBJECT_MAPPER.ensureJsonObjectsValid(databindPackageNames);
    }
}
