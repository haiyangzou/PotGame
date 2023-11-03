package org.pot.game.engine.march;

import org.pot.common.util.MapUtil;
import org.pot.game.engine.march.bean.MarchHeroBean;
import org.pot.game.engine.march.bean.MarchSoldierBean;
import org.pot.game.engine.march.bean.MarchTroopBean;
import org.pot.game.engine.player.Player;
import org.pot.message.protocol.world.MarchHero;
import org.pot.message.protocol.world.MarchSoldier;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MarchUtil {
    public static MarchTroopBean transformTroopBean(Player player, List<Integer> heroList, Map<Integer, Long> soldierList) {
        List<MarchHeroBean> heroBeans = transformHeroBean(player, heroList);
        List<MarchSoldierBean> soldierBeans = transformSoldierBean(player, soldierList);
        return new MarchTroopBean(heroBeans, soldierBeans);
    }

    public static List<MarchSoldierBean> transformSoldierBean(Player player, Map<Integer, Long> soldierMap) {
        return null;
    }

    public static List<MarchHeroBean> transformHeroBean(Player player, List<Integer> heroList) {
        return null;
    }

    public static MarchTroopBean transformTroopBean(Player player, List<MarchHero> heroList, List<MarchSoldier> soldierList) {
        List<Integer> h = heroList.stream().map(MarchHero::getHeroId).collect(Collectors.toList());
        Map<Integer, Long> m = MapUtil.toHashMap(soldierList, MarchSoldier::getSoldierId, marchSoldier -> (long) marchSoldier.getSoldierAmount());
        return transformTroopBean(player, h, m);
    }

}
