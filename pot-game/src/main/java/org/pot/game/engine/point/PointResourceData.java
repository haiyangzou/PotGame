package org.pot.game.engine.point;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.pot.game.engine.enums.PointType;
import org.pot.game.engine.march.March;
import org.pot.game.engine.march.MarchManager;
import org.pot.game.engine.scene.WorldPoint;
import org.pot.game.resource.GameConfigSupport;
import org.pot.game.resource.map.ResourceInfo;
import org.pot.game.resource.map.ResourceInfoConfig;

@Slf4j
@Getter
@JsonTypeName("WorldResource")
public class PointResourceData extends PointExtraData {
    @JsonProperty("resourceId")
    private int resourceId;
    @JsonProperty("amount")
    private volatile long amount;
    @JsonProperty("occupier")
    private int occupier;
    @JsonProperty("occupierMarchId")
    private String occupierMarchId;
    @JsonProperty("occupierGatherSpeed")
    private int occupierGatherSpeed;
    @JsonProperty("occupierGatherStartTime")
    private int occupierGatherStartTime;

    public PointResourceData(Integer resourceId) {
        super(PointType.RESOURCE);
        this.resourceId = resourceId;
        this.initialize();
    }

    private void initialize() {
        ResourceInfoConfig resourceInfoConfig = GameConfigSupport.getConfig(ResourceInfoConfig.class);
        ResourceInfo resourceInfo = resourceInfoConfig.getSpec(resourceId);
        this.amount = resourceInfo.getAmount();
        this.occupier = 0;
        this.occupierMarchId = "";
        this.occupierGatherSpeed = 0;
        this.occupierGatherStartTime = 0;
    }

    @Override
    public void validate(WorldPoint worldPoint) {
        super.validate(worldPoint);
        if (scene != null && StringUtils.isNotBlank(occupierMarchId)) {
            MarchManager marchManager = scene.getMarchManager();
            March march = marchManager.getMarch(occupierMarchId);
            if (march == null) {
                this.initialize();
            }
        }
    }

}
