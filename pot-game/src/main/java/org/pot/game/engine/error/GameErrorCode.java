package org.pot.game.engine.error;

import org.pot.common.concurrent.exception.IErrorCode;

public enum GameErrorCode implements IErrorCode {
    UNLOCK(10000),
    TIME_NOT_ENOUGH(10001),
    LEVEL_MAX(10002),
    ITEM_NOT_ENOUGH(10003),
    RESOURCE_NOT_ENOUGH(10004),
    CONDITION_NOT_ENOUGH(10005),
    PLAYER_NOT_FOUND(10006),
    PLAYER_NAME_IS_EXIST(10007),
    PLAYER_LEVEL_NOT_ENOUGH(10008),
    DIAMOND_NOT_ENOUGH(10009),
    ENERGY_NOT_ENOUGH(10010),
    BASE_DATA_NOT_EXIST(10100),
    BASE_DATA_ERROR(10101),
    BASE_PARAM_ERROR(10102),
    ILLEGAL_CHAR(10103),
    NO_HERO(20001),
    HERO_HAD_EXISTS(20002),
    MAX_RANK(20003),
    CAN_NOT_STRENGTHEN(20004),
    HERO_LEVEL_LIMIT(20005),
    LEVEL_NOT_ENOUGH(20006),
    STRENGTHEN_LEVEL_NOT_ENOUGH(20007),
    STAR_NOT_ENOUGH(20008),
    WEAR_EQUIP(20009),
    HERO_NOT_ALLOW_FRAGMENT(20010),
    HAD_GET_BACKGROUND_REWARD(20011),
    NO_BACKGROUND_REWARD(20012),
    HERO_IS_MARCHING(20013),
    HERO_RESET_ERROR(20014),
    HERO_ACTIVE(20015),
    HERO_FREE_TIME_NOT_ENOUGH(20016),
    HERO_NO_FREE_COUNT(20017),
    HERO_EXCHANGE_NOT_EXITS(20018),
    ITEM_ILLEGAL_REQUEST(20100),
    ITEM_CANT_USE(20101),
    ITEM_CANT_RECYCLE(20102),
    ITEM_BATCH_USE_LIMIT(20103),
    ITEM_NOT_ENOUGH_USE_CONDITION(20104),
    ITEM_NO_PROCESSOR(20105),
    ITEM_NO_EXISTS(20106),
    ITEM_SHOP_NO_EXISTS(20107),
    SHIELD_CAN_NOT_USE_BY_WAR(20108),
    USE_HAMMER_ERROR(20109),
    USE_ENERGY_LIMIT(20110),
    ITEM_RANDOM_MOVE_CITY_ERROR(20111),
    PRE_CONDITION_NOT_ENOUGH(20200),
    POSITION_ERROR(20201),
    POSITION_OCCUPY(20202),
    NOT_IDLE_QUEUE(20203),
    BUILDING_NOT_EXIST(20204),
    IN_BUILD(20205),
    BUILDING_QUEUE_TIME_NOT_ENOUGH(20206),
    BUILDING_COUNT_LIMIT(20207),
    BUILDING_LEVEL_LIMIT(20208),
    BUILDING_STATUS_ERROR(20209),
    BUILDING_TYPE_ERROR(20210),
    BUILDING_NOT_LEVEL_UP(20211),
    BUILDING_PARADE_GROUND_ERROR(20212),
    BUILDING_RADIO_TIME_NOT_ENOUGH(20213),
    BUILDING_SUPPLY_ERROR(20214),
    BUILDING_SUPPLY_RESOURCE_UNLOCK(20215),
    MYSTIC_SHOP_REFRESH_ERROR(20216),
    MYSTIC_SHOP_LOCK_ERROR(20217),
    MYSTIC_SHOP_TYPE_ERROR(20218),
    MYSTIC_SHOP_BUY(20219),
    BUILDING_CANNOT_MOVE(20220),
    BUILDING_CANNOT_CLEAN(20221),
    RADAR_EVENT_NOT_EXISTS(20222),
    RADAR_EVENT_STATUS_NOT_FINISH(20223),
    RADAR_MAX_STRENGTH_LEVEL(20224),
    BUILDING_PARADE_GROUND_NOT_EXIST(20225),
    NO_QUEUE_UUID(20300),
    USE_ITEM_TYPE_ERROR(20301),
    QUEUE_TIME_NOT_ENOUGH(20302),
    QUEUE_NO_NEED_HELP(20303),
    QUEUE_HELPED(20304),
    QUEUE_HELP_LIMIT(20305),
    QUEUE_ING(20306),
    QUEUE_LEFT_TIME_ERROR(20307),
    QUEUE_FREE_ERROR(20308),
    QUEUE_SECOND_BUILD_ERROR(20309),
    CAN_NOT_RECRUIT(20400),
    RECRUITING(20401),
    ARMY_NOT_EXISTS(20402),
    ARMY_DISMISS_ERROR(20403),
    LOW_ARMY_NOT_ENOUGH(20404),
    ARMY_UP_LEVEL_ERROR(20405),
    ARMY_RECRUIT_OVER_LIMIT(20406),
    ARMY_TREAT_NUM_ERROR(20407),
    TREATING(20408),
    ARMY_UNLOCK(20409),
    PLAYER_RESOURCE_USED(20500),
    PLAYER_NOT_RESOURCE_BUILDING(20501),
    TASK_NO_EXISTS(20600),
    TASK_FINISH(20601),
    TASK_NOT_RECEIVED(20602),
    TASK_NOT_FINISH(20603),
    TASK_STATUS_ERROR(20604),
    TASK_REWARD(20605),
    TASK_BOX_EMPTY(20606),
    TASK_BOX_SCORE_NO_LIMIT(20607),
    TASK_NO_REWARD(20608),
    SHOP_ILLEGAL_REQUEST(20701),
    SHOP_NOT_ENOUGH_STOCK(20702),
    SHOP_NOT_UNLOCKED(20703),
    SHOP_BUY_INSUFFICIENT_CONDITIONS(20704),
    SHOP_INSUFFICIENT_EXPENSES(20705),
    SHOP_ITEM_NOT_EXISTS(20706),
    TECH_PRE_CONDITION_NOT_ENOUGH(20800),
    TECH_MAX_LEVEL(20801),
    TECH_ING(20802),
    CITY_WALL_NOT_FOUND(20900),
    CITY_WALL_INVALID_STATUS(20901),
    CITY_WALL_INVALID_REPAIR_TIME(20902),
    TALENT_PRE_CONDITION_NOT_ENOUGH(21000),
    TALENT_MAX_LEVEL(21001),
    TALENT_POINT_NOT_ENOUGH(21002),
    TALENT_TEMPLATE_ERROR(21003),
    TALENT_SKILL_NOT_EXISTS(21004),
    TALENT_SKILL_TIME_NOT_ENOUGH(21005),
    TALENT_SKILL_NOT_ACTIVE(21006),
    ICON_NOT_HAVE(21100),
    FRAME_NOT_HAVE(21101),
    FAVORITES_NOT_EXISTS(21102),
    FAVORITES_SIZE_LIMIT(21103),
    FAVORITES_SIZE_MAX_LIMIT(21104),
    NAME_CONTAIN_SENSITIVE_WORD(21105),
    NAME_OCCUPIED(21106),
    NAME_ILLEGAL(21107),
    DAILY_LOGIN_REWARDED(21108),
    NO_HERO_SET(21200),
    PUZZLE_BATTLE_OVER(21201),
    PUZZLE_BATTLE_IN_PROGRESS(21202),
    PRE_LEVEL_LIMIT(21203),
    DEAD_CANNOT_FIRE_SKILL(21204),
    SILENT_FIRE_SKILL(21205),
    MP_NOT_ENOUGH(21206),
    CHAT_UNION_NO_EXISTS(21300),
    CHAT_CONTENT_LIMIT(21301),
    CHAT_SEND_TIME_LIMIT(21302),
    CHAT_SHARE_BATTLE_TIME(21303),
    CHAT_SEND_SAME_CONTENT_TIME_LIMIT(21304),
    DRAGON_FREE_TIME_ERROR(21500),
    DRAGON_SKILL_MAX_LEVEL(21501),
    DRAGON_FREE_COUNT_NOT_ENOUGH(21502),
    DRAGON_HAVE(21503),
    DRAGON_SKILL_NOT_EXISTS(21504),
    DRAGON_NOT_ACTIVATED(21505),
    DRAGON_LEVEL_NOT_ENOUGH(21506),
    REINFORCEMENT_CASTLE_LEVEL_LIMIT(21601),
    REINFORCEMENT_CAPACITY_LIMIT(21602),
    REINFORCEMENT_NO_EMBASSY(21603),
    UNION_TRADE_NO_MARKET(21604),
    UNION_TRADE_CAPACITY_LIMIT(21605),
    CANNOT_ATTACK_SELF(21606),
    CANNOT_ATTACK_ALLY(21607),
    TARGET_HAS_SHIELD(21608),
    RANK_ID_NOT_EXISTS(21700),
    RANK_LEVEL_LIMIT(21701),
    ACTIVITY_STATUS_ERROR(40000),
    ACTIVITY_BOX_REWARD(40001),
    ACTIVITY_BOX_CONDITION_ERROR(40002),
    ACTION_NOT_EXISTS(50000),
    ACTION_TYPE_ERROR(50001),
    BUILDING_REPAIR_FINISH(50002),
    WORLD_MAP_VIEW_LEVEL_NOT_EXISTS(80000),
    WORLD_MAP_RELOCATE_TYPE_ERROR(80001),
    WORLD_MAP_RELOCATE_POINT_ERROR(80002),
    MARCH_NOT_EXISTS(81000),
    MARCH_SOLDIER_EMPTY(81001),
    MARCH_SOLDIER_NOT_ENOUGH(81002),
    MARCH_SOLDIER_OVER_LIMIT(81003),
    MARCH_HERO_NOT_EXISTS(81004),
    MARCH_HERO_IS_MARCHING(81005),
    MARCH_SCOUT_TARGET_TYPE_ERROR(81006),
    MARCH_GATHER_TARGET_TYPE_ERROR(81007),
    MARCH_GATHER_TARGET_LEVEL_ERROR(81008),
    MARCH_ATTACK_TARGET_TYPE_ERROR(81009),
    MARCH_DISTANCE_LIMIT(81010),
    MARCH_EXCEED_AMOUNT_LIMIT(81011),
    MARCH_ATTACK_MONSTER_LEVEL_ERROR(81012),
    MARCH_GATHER_RETURN_TYPE_ERROR(81013),
    MARCH_SCOUT_WATCHTOWER_LEVEL_ERROR(81014),
    MARCH_DISTANCE_TOO_FAR_ERROR(81015),
    MARCH_ATTACK_MONSTER_POWER_ERROR(81016),
    MARCH_FIGHTING(81017),
    WORLD_MAP_SEARCH_POINT_NOT_EXISTS(82001),
    WORLD_MAP_SEARCH_TYPE_NOT_EXISTS(82002),
    WORLD_MAP_SEARCH_MAX_LEVEL(82003);
    ;
    public final int errorCode;

    GameErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String getErrorName() {
        return name();
    }
}
