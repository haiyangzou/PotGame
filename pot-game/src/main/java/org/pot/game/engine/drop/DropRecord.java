package org.pot.game.engine.drop;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.pot.common.databind.json.JsonObject;

import java.io.Serializable;
import java.time.LocalDate;

@Setter
@Getter
public class DropRecord implements Serializable, JsonObject {
    @JsonProperty("dropId")
    String dropId = "";

    @JsonProperty("count")
    int count = 0;


    @JsonProperty("date")
    LocalDate date = LocalDate.now();

    @JsonProperty("timePeriod")
    int timePeriod = 1;

    public DropRecord(String dropId, LocalDate date, int timePeriod) {
        this.dropId = dropId;
        this.date = date;
        this.timePeriod = timePeriod;
    }
}
