package org.pot.game.engine.enums;

import lombok.Getter;

@Getter
public enum RewardSourceType {
    COMMON(0, RewardShowType.FLY, true),
    HERO_DRAW(1, RewardShowType.FLY, false),
    CHALLENGE_PUZZLE_LEVEL(2, RewardShowType.FLY, false),
    PASS_PUZZLE_LEVEl(3, RewardShowType.FLY, false),
    MOP_UP_PUZZLE_LEVEL(4, RewardShowType.FLY, false),
    CREATE_UNION_COST(5, RewardShowType.FLY, false),
    CHANGE_UNION_NAME(6, RewardShowType.FLY, false),
    CHANGE_UNION_ALIAS(7, RewardShowType.FLY, false),
    FIRST_JOIN_UNION_CLAIM(8, RewardShowType.FLY, false),
    DONATE_UNION_TECH(9, RewardShowType.FLY, false),
    UNION_HELP(10, RewardShowType.FLY, false),
    UNION_SHOP_BUY_GOODS(11, RewardShowType.FLY, false),
    SYS_MAIL_REWARD(12, RewardShowType.SPRINGBOARD, false),
    CLAIM_UNION_GIFT(13, RewardShowType.FLY, false),
    DAILY_UNION_TECH_RANK(14, RewardShowType.FLY, false),
    UNION_ACTIVE_BOX(15, RewardShowType.FLY, false),
    UNION_TRADE(16, RewardShowType.FLY, false),
    UNION_RELOCATE(17, RewardShowType.FLY, false),
    CHAPTER_BOX_REWARD(18, RewardShowType.SPRINGBOARD, false),
    USE_BOX_ITEM(19, RewardShowType.SPRINGBOARD, false),
    CASTLE_UPGRADE(20, RewardShowType.SPRINGBOARD, true),
    PLAYER_UPGRADE(21, RewardShowType.SPRINGBOARD, true),
    DRAGON_UPGRADE(22, RewardShowType.SPRINGBOARD, true),
    MEGA_RALLY(23, RewardShowType.FLY, false),
    ASSEMBLE(24, RewardShowType.FLY, false),
    ATTACK_LAIR(25, RewardShowType.FLY, false),
    ATTACK_CITY(26, RewardShowType.FLY, false),
    SPECIAL_ITEM(27, RewardShowType.SPRINGBOARD, true),
    INNER_BATTLE(28, RewardShowType.FLY, true),
    ATTACK_MONSTER(30, RewardShowType.FLY, true),
    POWER_RECOVER(32, RewardShowType.FLY, true),
    ENERGY_RECOVER(33, RewardShowType.FLY, true),
    RADAR_RESCUE(34, RewardShowType.FLY, true),
    RADAR_MONSTER(35, RewardShowType.FLY, true),
    RADAR_PUZZLE(36, RewardShowType.FLY, true),
    QUIZ(37, RewardShowType.FLY, true),
    TECH_SPEED_UP(38, RewardShowType.FLY, true),
    SHARE_MAIL(39, RewardShowType.FLY, true),
    MARCH_RECALL(40, RewardShowType.FLY, true),
    MARCH_SPEED(41, RewardShowType.FLY, true),
    SCOUT(42, RewardShowType.FLY, true),
    WORLD_CITY_RELOCATE(43, RewardShowType.FLY, true),
    ARMY_RECRUIT(44, RewardShowType.FLY, true),
    TREAT_ARMY(45, RewardShowType.FLY, true),
    ARMY_UP_LEVEL(46, RewardShowType.FLY, true),
    CREATE_BUILDING(47, RewardShowType.FLY, true),
    UPGRADE_BUILDING(48, RewardShowType.FLY, true),
    REPAIR_BUILDING(49, RewardShowType.FLY, true),
    BUY_MYSTIC_NORMAL_SHOP(50, RewardShowType.FLY, true),
    BUY_MYSTIC_DIAMOND_SHOP(51, RewardShowType.FLY, true),
    SEND_CHAT_BROADCAST(52, RewardShowType.FLY, true),
    STOP_BURN(53, RewardShowType.FLY, true),
    COMMANDER_RENAME(54, RewardShowType.FLY, true),
    FAVORITES_SIZE_ADD(55, RewardShowType.FLY, true),
    DRAGON_SKILL_UPGRADE(56, RewardShowType.FLY, true),
    DRAGON_SKILL_RESET(57, RewardShowType.FLY, true),
    ACTIVE_HERO(59, RewardShowType.FLY, true),
    RESET_HERO(60, RewardShowType.FLY, true),
    HERO_FRAG_RECYCLE(61, RewardShowType.FLY, true),
    HERO_FRAG_EXCHANGE(62, RewardShowType.FLY, true),
    EXCHANGE_HERO_FRAG(63, RewardShowType.FLY, true),
    QUEUE_COMPLETE(65, RewardShowType.FLY, true),
    RESOURCE_ITEM_SPEED_UP(66, RewardShowType.FLY, true),
    RESOURCE_DIAMOND_SPEED_UP(67, RewardShowType.FLY, true),
    RESET_TALENT(68, RewardShowType.FLY, true),
    SUPPLY_CHANGE(69, RewardShowType.FLY, true),
    DRAGON_ACTIVE(70, RewardShowType.FLY, true),
    HERO_UPGRADE_LEVEL(71, RewardShowType.FLY, true),
    HERO_UPGRADE_RANK(72, RewardShowType.FLY, true),
    HERO_UPGRADE_STRENGTHEN_LEVEL(73, RewardShowType.FLY, true),
    HERO_UPGRADE_STAR(74, RewardShowType.FLY, true),
    DRAW_HERO(75, RewardShowType.FLY, true),
    DIAMOND_SHOP(76, RewardShowType.FLY, true),
    SEVEN_DAY_TASK_REWARD(77, RewardShowType.FLY, true),
    SEVEN_DAY_BOX_REWARD(78, RewardShowType.FLY, true),
    CLEAN_BUILDING(79, RewardShowType.FLY, true),
    NEW_HAND_REWARD(80, RewardShowType.FLY, true),
    DAILY_LOGIN_REWARD(81, RewardShowType.FLY, true),
    DAILY_WORSHIP(82, RewardShowType.FLY, true),
    DRAGON_INTERACTIVE(83, RewardShowType.FLY, true),
    UPGRADE_LEVEL_ONE_KEY(84, RewardShowType.FLY, true),
    HERO_BACKGROUND_REWARD(85, RewardShowType.FLY, true),
    UPGRADE_RADAR_STRENGTH(86, RewardShowType.FLY, true),
    CHAPTER_TASK_REWARD(87, RewardShowType.FLY, true),
    DAILY_TASK_REWARD(88, RewardShowType.FLY, true),
    DAILY_TASK_BOX_REWARD(89, RewardShowType.FLY, true),
    MAIN_TASK_REWARD(90, RewardShowType.FLY, true),
    RECYCLE_ITEM(91, RewardShowType.FLY, true),
    PLUNDER(92, RewardShowType.FLY, true),
    PLUNDERED(93, RewardShowType.FLY, true),
    GATHER_RESOURCE(94, RewardShowType.FLY, true),
    REWARD_HERO(95, RewardShowType.FLY, true),
    TAKE_RESOURCE(96, RewardShowType.FLY, true),
    SKILL_TAKE_ALL_RESOURCE(97, RewardShowType.FLY, true),
    QUIZ_SUM_REWARD(98, RewardShowType.FLY, true),
    BUILDING_QUEUE_SPEED_UP(99, RewardShowType.FLY, true),
    TRAIN_QUEUE_SPEED_UP(100, RewardShowType.FLY, true),
    RESEARCH_QUEUE_SPEED_UP(101, RewardShowType.FLY, true),
    TREAT_QUEUE_SPEED_UP(102, RewardShowType.FLY, true),
    IMMEDIATE_UPGRADE_BUILDING(103, RewardShowType.FLY, true),
    IMMEDIATE_TREAT_ARMY(104, RewardShowType.FLY, true),
    IMMEDIATE_ARMY_RECRUIT(105, RewardShowType.FLY, true),
    IMMEDIATE_TECH_SPEED_UP(106, RewardShowType.FLY, true),
    USE_RESOURCE_ITEM(107, RewardShowType.FLY, true),
    CHALLENGE_PUZZLE_LEVEL_FAIL(108, RewardShowType.FLY, false);
    final int sourceType;

    final RewardShowType showType;

    final boolean needCombine;

    RewardSourceType(int sourceType, RewardShowType showType, boolean needCombine) {
        this.sourceType = sourceType;
        this.showType = showType;
        this.needCombine = needCombine;
    }
}
