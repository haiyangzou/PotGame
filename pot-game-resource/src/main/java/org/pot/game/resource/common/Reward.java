package org.pot.game.resource.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.pot.common.databind.json.JsonObject;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = {"type", "id"})
public class Reward implements Comparable<Reward>, JsonObject, Serializable {
    @JsonProperty("type")
    private int type;
    @JsonProperty("id")
    private String id;
    @JsonProperty("count")
    private long count;

    public Reward(int type, long count) {
        this.type = type;
        this.id = "";
        this.count = count;
    }

    @JsonCreator
    public Reward(@JsonProperty("id") String id, @JsonProperty("type") int type, @JsonProperty("count") long count) {
        this.type = type;
        this.id = StringUtils.defaultString(id);
        this.count = count;
    }

    @Override
    public int compareTo(Reward o) {
        return 0;
    }
}
