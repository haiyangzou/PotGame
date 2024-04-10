package org.pot.game.resource.hero;

import lombok.Getter;
import org.pot.game.resource.InitJsonConfigSpec;

@Getter
public class HeroInfo extends InitJsonConfigSpec {
    private String name;

    private int armyType;

    private int color;

    private int star;

    private int maxLevel;

    private int abilityBase;

    private double abilityRatio;

    private int attackCd;

    private int skillSpeed;

    private int baseAtk;

    private int baseDef;

    private int baseHp;

    private int mpMax;

    private double baseHit;

    private double baseDodge;

    private double baseCrit;

    private double antiCrit;

    private double avoidCtrl;

    private double heal;

    private double mpRecover;

    private int mpAtk;

    private int mpDef;

    private int mpCd;

    private double levelAtk;

    private double levelDef;

    private double levelHp;

    private int corpSkillCd;

    private int initType;

    private String model;

    private String medalId;

    private String fragmentItemId;

    private int medalAmount;

    private String giftId;

    private int profileId;
}
