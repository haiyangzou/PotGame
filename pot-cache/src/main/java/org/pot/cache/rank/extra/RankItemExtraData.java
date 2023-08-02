package org.pot.cache.rank.extra;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.pot.common.databind.json.JsonObject;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
public class RankItemExtraData implements JsonObject, Serializable {
    public RankItemExtraData() {
    }
}
