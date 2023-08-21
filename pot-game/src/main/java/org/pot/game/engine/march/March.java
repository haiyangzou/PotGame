package org.pot.game.engine.march;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.pot.common.databind.json.JsonObject;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
public interface March extends JsonObject, Serializable {
    String getId();

    long getOwnerId();

    void onAdd(MarchManager marchManager);

    void onRemove(MarchManager marchManager);

    MarchManager getManager();

    void onInitPlayerData();

    int getTargetPoint();
}
