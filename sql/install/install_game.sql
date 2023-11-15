/*
 Navicat Premium Data Transfer

 Source Server         : k2_wifi
 Source Server Type    : MySQL
 Source Server Version : 80028
 Source Host           : 172.16.20.121:3306
 Source Schema         : magics-game

 Target Server Type    : MySQL
 Target Server Version : 80028
 File Encoding         : 65001

 Date: 14/11/2023 18:58:52
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for activity
-- ----------------------------
DROP TABLE IF EXISTS `activity`;
CREATE TABLE `activity`  (
  `id` int(0) NOT NULL COMMENT '活动id',
  `data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '活动数据',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '活动信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for artifact
-- ----------------------------
DROP TABLE IF EXISTS `artifact`;
CREATE TABLE `artifact`  (
  `id` int(0) NOT NULL COMMENT '神器ID',
  `artifact_data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '神器信息',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '神器表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for drop_record
-- ----------------------------
DROP TABLE IF EXISTS `drop_record`;
CREATE TABLE `drop_record`  (
  `id` bigint(0) NOT NULL COMMENT '主键 玩家id (-1表示全服)',
  `detail` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '掉落数据',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '掉落记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_activity
-- ----------------------------
DROP TABLE IF EXISTS `player_activity`;
CREATE TABLE `player_activity`  (
  `player_id` bigint(0) NOT NULL COMMENT '主键 玩家id',
  `activity_type` int(0) NOT NULL COMMENT '主键 活动类型',
  `data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '活动数据',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`player_id`, `activity_type`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '玩家活动信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_appearance
-- ----------------------------
DROP TABLE IF EXISTS `player_appearance`;
CREATE TABLE `player_appearance`  (
  `id` bigint(0) NOT NULL COMMENT '主键 玩家id',
  `appearance_detail` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '主城外观详情信息',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '玩家外观(皮肤，氛围，铭牌，行军)信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_arena
-- ----------------------------
DROP TABLE IF EXISTS `player_arena`;
CREATE TABLE `player_arena`  (
  `player_id` bigint(0) NOT NULL COMMENT '玩家ID',
  `detail` json NULL COMMENT '详情',
  `team_infos` json NULL COMMENT 'team_infos',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`player_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '竞技场' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_army
-- ----------------------------
DROP TABLE IF EXISTS `player_army`;
CREATE TABLE `player_army`  (
  `id` bigint(0) NOT NULL COMMENT '主键 玩家id',
  `army_detail` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '军队信息',
  `hurt_detail` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '伤兵信息',
  `treat_detail` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '治疗信息',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '玩家兵种表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_buff
-- ----------------------------
DROP TABLE IF EXISTS `player_buff`;
CREATE TABLE `player_buff`  (
  `id` bigint(0) NOT NULL COMMENT '主键 玩家id',
  `buff_info` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Buff数据',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '玩家Buff表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_building
-- ----------------------------
DROP TABLE IF EXISTS `player_building`;
CREATE TABLE `player_building`  (
  `id` bigint(0) NOT NULL COMMENT '主键 玩家id',
  `building_info` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '建筑信息',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '玩家建筑表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_common
-- ----------------------------
DROP TABLE IF EXISTS `player_common`;
CREATE TABLE `player_common`  (
  `id` bigint(0) NOT NULL COMMENT '主键 玩家id',
  `profile_icon_id` int(0) NOT NULL DEFAULT 0 COMMENT '头像ICON',
  `profile_frame_id` int(0) NOT NULL DEFAULT 0 COMMENT '头像框ID',
  `profile_picture` blob NULL COMMENT '自定义头像',
  `level` int(0) NOT NULL DEFAULT 1 COMMENT '玩家等级',
  `exp` int(0) NOT NULL DEFAULT 0 COMMENT '玩家经验',
  `power_refresh_time` bigint(0) NOT NULL COMMENT '体力刷新时间',
  `energy_refresh_time` bigint(0) NOT NULL COMMENT '行动力刷新时间',
  `picture_refresh_time` bigint(0) NOT NULL COMMENT '自定义头像刷新时间',
  `frame_detail` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '头像框信息',
  `icon_detail` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '头像信息',
  `statistics_detail` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '统计信息',
  `blacklist` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `favorites_detail` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '收藏夹信息',
  `forbidden_player` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '屏蔽玩家列表',
  `sys_mail_id` bigint(0) NOT NULL DEFAULT 0 COMMENT '上一封发送系统邮件',
  `app_push` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'app推送设置',
  `display_language` tinyint(0) NOT NULL DEFAULT 0 COMMENT '是否展示交流语言设置(0关闭,1开启)',
  `prioritize_non_troop_target` tinyint(0) NOT NULL DEFAULT 0 COMMENT '优先选择非部队目标设置(0关闭,1开启)',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '玩家基础信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_country_hospital
-- ----------------------------
DROP TABLE IF EXISTS `player_country_hospital`;
CREATE TABLE `player_country_hospital`  (
  `id` bigint(0) NOT NULL COMMENT '主键 玩家id',
  `treat_detail` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '治疗中军队信息',
  `treat_start_time` bigint(0) NOT NULL COMMENT '治疗开始时间',
  `treat_end_time` bigint(0) NOT NULL COMMENT '治疗原始结束时间',
  `treat_real_end_time` bigint(0) NOT NULL COMMENT '治疗真实结束时间',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '玩家国家医院表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_cr_level
-- ----------------------------
DROP TABLE IF EXISTS `player_cr_level`;
CREATE TABLE `player_cr_level`  (
  `player_id` bigint(0) NOT NULL DEFAULT 0 COMMENT '玩家ID',
  `max_level_id` int(0) NOT NULL DEFAULT 0 COMMENT '玩家ID',
  `level_data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '关卡详情',
  `star_reward_state` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '关卡详情',
  `new_level_data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '关卡详情',
  `energy` int(0) NOT NULL DEFAULT 0 COMMENT '玩家ID',
  `cd_time` bigint(0) NOT NULL DEFAULT 0 COMMENT '章节星级奖励领取状态',
  `transfer` int(0) NOT NULL,
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`player_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '玩家数字门关卡信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_dragon
-- ----------------------------
DROP TABLE IF EXISTS `player_dragon`;
CREATE TABLE `player_dragon`  (
  `id` bigint(0) NOT NULL COMMENT '主键 玩家id',
  `dragon_id` int(0) NULL DEFAULT NULL COMMENT '出战龙id',
  `dragon_skin_id` int(0) NULL DEFAULT NULL COMMENT '内城龙皮肤id',
  `level` int(0) NULL DEFAULT NULL COMMENT '龙等级',
  `dragon_exp` bigint(0) NULL DEFAULT NULL COMMENT '龙经验',
  `free_count` int(0) NULL DEFAULT NULL COMMENT '免费次数',
  `refresh_time` bigint(0) NOT NULL COMMENT '免费冷却时间',
  `dragon_detail` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '已激活龙宠详细信息',
  `equip_detail` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `skill_detail` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '详情信息',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '玩家生化人（龙宠）信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_equipment
-- ----------------------------
DROP TABLE IF EXISTS `player_equipment`;
CREATE TABLE `player_equipment`  (
  `id` bigint(0) NOT NULL COMMENT '主键',
  `equipment_slot` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '装备数据',
  `equipment_info` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '装备背包数据',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '玩家装备数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_facebook
-- ----------------------------
DROP TABLE IF EXISTS `player_facebook`;
CREATE TABLE `player_facebook`  (
  `id` bigint(0) NOT NULL COMMENT '主键',
  `reward_id` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '已领取奖励',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '玩家facebook粉丝奖励' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_ghost
-- ----------------------------
DROP TABLE IF EXISTS `player_ghost`;
CREATE TABLE `player_ghost`  (
  `player_id` bigint(0) NOT NULL COMMENT '主键 玩家id',
  `destroyed` tinyint(0) NOT NULL COMMENT '销毁状态',
  `visa_data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '跨服数据',
  `source_server_type_id` int(0) NOT NULL COMMENT '来源服务器类型',
  `source_server_id` int(0) NOT NULL COMMENT '来源服务器ID',
  `target_server_type_id` int(0) NOT NULL COMMENT '目标服务器类型',
  `target_server_id` int(0) NOT NULL COMMENT '目标服务器ID',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`player_id`) USING BTREE,
  INDEX `idx_destroyed`(`destroyed`) USING BTREE,
  INDEX `idx_source_server`(`source_server_type_id`, `source_server_id`, `destroyed`) USING BTREE,
  INDEX `idx_target_server`(`target_server_type_id`, `target_server_id`, `destroyed`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '玩家幽灵表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_gift
-- ----------------------------
DROP TABLE IF EXISTS `player_gift`;
CREATE TABLE `player_gift`  (
  `player_id` bigint(0) NOT NULL COMMENT '玩家ID',
  `gift` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '礼包数据',
  `gift_shop` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '礼包商城数据',
  `gift_progress` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '礼包商城进度数据',
  `gift_purchase_order` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '最近一段时间购买的礼包订单数据',
  `gift_history_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '历史购买礼包数量',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`player_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '玩家礼包商城数据' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_glory
-- ----------------------------
DROP TABLE IF EXISTS `player_glory`;
CREATE TABLE `player_glory`  (
  `id` bigint(0) NOT NULL COMMENT '主键 玩家id',
  `building_id` int(0) NOT NULL DEFAULT 0 COMMENT '建筑id',
  `glory_level` int(0) NOT NULL DEFAULT 0 COMMENT '荣耀等级',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`, `building_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '玩家荣耀表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_hero
-- ----------------------------
DROP TABLE IF EXISTS `player_hero`;
CREATE TABLE `player_hero`  (
  `id` bigint(0) NOT NULL COMMENT '主键 玩家id',
  `hero_info` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '英雄数据',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '玩家英雄表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_hero_gear
-- ----------------------------
DROP TABLE IF EXISTS `player_hero_gear`;
CREATE TABLE `player_hero_gear`  (
  `id` bigint(0) NOT NULL,
  `hero_gear_info` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `ordinary_forge_base_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `ordinary_forge_start_time` bigint(0) NULL DEFAULT NULL,
  `ordinary_forge_end_time` bigint(0) NULL DEFAULT NULL,
  `exclusive_forge_base_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `exclusive_forge_start_time` bigint(0) NULL DEFAULT NULL,
  `exclusive_forge_end_time` bigint(0) NULL DEFAULT NULL,
  `last_production_end_time` bigint(0) NULL DEFAULT NULL,
  `total_forge_count` int(0) NULL DEFAULT NULL,
  `today_complete_production_count` int(0) NULL DEFAULT NULL,
  `today_complete_forge_count` int(0) NULL DEFAULT NULL,
  `share_time` bigint(0) NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_holy
-- ----------------------------
DROP TABLE IF EXISTS `player_holy`;
CREATE TABLE `player_holy`  (
  `id` bigint(0) NOT NULL COMMENT '主键',
  `holy_slot` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '圣石数据',
  `holy_info` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '圣石背包数据',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '玩家圣石数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_item
-- ----------------------------
DROP TABLE IF EXISTS `player_item`;
CREATE TABLE `player_item`  (
  `player_id` bigint(0) NOT NULL COMMENT '主键 玩家id',
  `items` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '玩家的道具信息',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`player_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '玩家道具表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_mine
-- ----------------------------
DROP TABLE IF EXISTS `player_mine`;
CREATE TABLE `player_mine`  (
  `player_id` bigint(0) NOT NULL COMMENT '主键 玩家id',
  `zoneId` int(0) NOT NULL COMMENT '当前所在战区',
  `mineLevel` int(0) NULL DEFAULT NULL COMMENT '当前所在矿区层数 （-1表示未进入矿区）',
  `rest` tinyint(0) NULL DEFAULT NULL COMMENT '休整中',
  `restEndTime` bigint(0) NULL DEFAULT NULL COMMENT '休整结束时间戳',
  `autoOccupyEndTime` bigint(0) NULL DEFAULT NULL COMMENT '自动占领冷却到期时间',
  `maxLevel` int(0) NULL DEFAULT NULL COMMENT '已通过最高层数',
  `plunderCount` int(0) NULL DEFAULT NULL COMMENT '掠夺次数',
  `mineEndTime` bigint(0) NULL DEFAULT NULL COMMENT '矿区滞留到期时间',
  `gainStartTime` bigint(0) NULL DEFAULT NULL COMMENT '采集开始时间',
  `gainEndTime` bigint(0) NULL DEFAULT NULL COMMENT '采集结束时间',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`player_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '玩家矿井信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_new_hand
-- ----------------------------
DROP TABLE IF EXISTS `player_new_hand`;
CREATE TABLE `player_new_hand`  (
  `id` bigint(0) NOT NULL COMMENT '主键 玩家id',
  `var_detail` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '新手引导变量数据',
  `unlock_fog` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '未解锁迷雾',
  `guide_finish` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '结束的新手引导',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '新手信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_profile
-- ----------------------------
DROP TABLE IF EXISTS `player_profile`;
CREATE TABLE `player_profile`  (
  `uid` bigint(0) NOT NULL COMMENT 'uid',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'name',
  `account` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'account',
  `account_uid` bigint(0) NOT NULL COMMENT 'account_uid',
  `device` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '设备的唯一标识',
  `server_id` int(0) NOT NULL COMMENT '服务器ID',
  `union_id` int(0) NOT NULL DEFAULT 0 COMMENT '联盟ID',
  `reg_time` bigint(0) NOT NULL COMMENT '注册时间(UTC timestamp)',
  `reg_device_os` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '注册设备操作系统类型',
  `login_ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '登录IP',
  `login_device_os` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '登录设备操作系统类型',
  `last_login_time` bigint(0) NOT NULL COMMENT '最后登录时间(UTC timestamp)',
  `last_online_time` bigint(0) NOT NULL COMMENT '最后在线时间(UTC timestamp)',
  `online_time` bigint(0) NOT NULL DEFAULT 0 COMMENT '总在线时长(秒)',
  `world_point_id` int(0) NOT NULL COMMENT '世界坐标',
  `world_clean_time` bigint(0) NOT NULL DEFAULT 0 COMMENT '世界清除时间(UTC timestamp)',
  `pay_first_time` bigint(0) NOT NULL DEFAULT 0 COMMENT '首次充值时间(UTC timestamp)',
  `pay_total_price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '总充金额, 一般以美金计价',
  `pay_total_diamond` bigint(0) NOT NULL DEFAULT 0 COMMENT '总充钻石',
  `app_version` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '应用版本',
  `os_version` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '操作系统版本',
  `channel` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `platform` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `country` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `language` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `cid` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '个推标识',
  `chat_ban` bigint(0) NOT NULL DEFAULT 0 COMMENT '禁言截止时间',
  `rename_ban` bigint(0) NOT NULL DEFAULT 0 COMMENT '禁止改名截止时间',
  `translate_language` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '翻译语言',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `idx_name`(`name`) USING BTREE,
  INDEX `idx_account`(`account`) USING BTREE,
  INDEX `idx_account_uid`(`account_uid`) USING BTREE,
  INDEX `idx_device`(`device`) USING BTREE,
  INDEX `idx_server_id`(`server_id`) USING BTREE,
  INDEX `idx_union_id`(`union_id`) USING BTREE,
  INDEX `idx_reg_time`(`reg_time`) USING BTREE,
  INDEX `idx_last_login_time`(`last_login_time`) USING BTREE,
  INDEX `idx_last_online_time`(`last_online_time`) USING BTREE,
  INDEX `idx_online_time`(`online_time`) USING BTREE,
  INDEX `idx_world_point_id`(`world_point_id`) USING BTREE,
  INDEX `idx_world_clean_time`(`world_clean_time`) USING BTREE,
  INDEX `idx_pay_first_time`(`pay_first_time`) USING BTREE,
  INDEX `idx_pay_total_price`(`pay_total_price`) USING BTREE,
  INDEX `idx_pay_total_diamond`(`pay_total_diamond`) USING BTREE,
  INDEX `idx_app_version`(`app_version`) USING BTREE,
  INDEX `idx_channel`(`channel`) USING BTREE,
  INDEX `idx_platform`(`platform`) USING BTREE,
  INDEX `idx_country`(`country`) USING BTREE,
  INDEX `idx_language`(`language`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '玩家角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_puzzle_level
-- ----------------------------
DROP TABLE IF EXISTS `player_puzzle_level`;
CREATE TABLE `player_puzzle_level`  (
  `player_id` bigint(0) NOT NULL DEFAULT 0 COMMENT '玩家ID',
  `record_time` bigint(0) NULL DEFAULT NULL COMMENT '刷新记录时间',
  `reset_time` bigint(0) NULL DEFAULT NULL COMMENT '上次挑战次数重置时间',
  `detail` json NULL COMMENT '关卡详情',
  `timeline_hero` json NULL COMMENT '已播放timeline的heroId',
  `level_star` json NULL COMMENT '关卡星级',
  `star_reward_state` json NULL COMMENT '章节星级奖励领取状态',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`player_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '玩家三消关卡信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_puzzle_team
-- ----------------------------
DROP TABLE IF EXISTS `player_puzzle_team`;
CREATE TABLE `player_puzzle_team`  (
  `player_id` bigint(0) NOT NULL COMMENT '玩家ID',
  `team` json NOT NULL COMMENT '阵容数据',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`player_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '玩家阵容数据' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_queue
-- ----------------------------
DROP TABLE IF EXISTS `player_queue`;
CREATE TABLE `player_queue`  (
  `id` bigint(0) NOT NULL COMMENT '主键 玩家id',
  `queue_info` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '队列数据',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '玩家队列表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_refine
-- ----------------------------
DROP TABLE IF EXISTS `player_refine`;
CREATE TABLE `player_refine`  (
  `id` bigint(0) NOT NULL COMMENT '主键',
  `refine_type` int(0) NOT NULL DEFAULT 0 COMMENT '精造类型',
  `refine_level` int(0) NOT NULL DEFAULT 0 COMMENT '精造等级',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`, `refine_type`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '玩家精造数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_resource
-- ----------------------------
DROP TABLE IF EXISTS `player_resource`;
CREATE TABLE `player_resource`  (
  `id` bigint(0) NOT NULL COMMENT '主键 玩家id',
  `resource_info` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '资源数据',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '玩家资源表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_rune
-- ----------------------------
DROP TABLE IF EXISTS `player_rune`;
CREATE TABLE `player_rune`  (
  `id` bigint(0) NOT NULL COMMENT '主键',
  `rune_slot` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '穿戴的芯片数据',
  `rune_info` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '芯片背包数据',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '玩家芯片数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_shopping_info
-- ----------------------------
DROP TABLE IF EXISTS `player_shopping_info`;
CREATE TABLE `player_shopping_info`  (
  `id` bigint(0) NOT NULL COMMENT '玩家id',
  `shopping_info` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '购物信息',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '玩家购物信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_talent
-- ----------------------------
DROP TABLE IF EXISTS `player_talent`;
CREATE TABLE `player_talent`  (
  `id` bigint(0) NOT NULL COMMENT '主键 玩家ID',
  `index` int(0) NOT NULL DEFAULT 1 COMMENT '使用的天赋模板信息',
  `talent_detail` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '天赋详情信息',
  `skill_detail` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '天赋技能详情信息',
  `template_detail` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '天赋模板详情信息',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '玩家天赋' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_task_chapter
-- ----------------------------
DROP TABLE IF EXISTS `player_task_chapter`;
CREATE TABLE `player_task_chapter`  (
  `id` bigint(0) NOT NULL COMMENT '主键 玩家id',
  `chapter_id` int(0) NOT NULL COMMENT '当前章节',
  `finish_chapter_task` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '已完成的章节任务id',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '玩家章节任务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_task_daily
-- ----------------------------
DROP TABLE IF EXISTS `player_task_daily`;
CREATE TABLE `player_task_daily`  (
  `id` bigint(0) NOT NULL COMMENT '主键 玩家id',
  `daily` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '每日任务宝箱',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '玩家每日任务' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_task_dimensional
-- ----------------------------
DROP TABLE IF EXISTS `player_task_dimensional`;
CREATE TABLE `player_task_dimensional`  (
  `id` bigint(0) NOT NULL COMMENT '主键 玩家id',
  `group_id` int(0) NOT NULL COMMENT '当前任务组',
  `task_status` int(0) NOT NULL COMMENT '当前任务组状态',
  `task_count_down_time` bigint(0) NOT NULL COMMENT '当前任务限时奖励截止时间',
  `task_finish_time` bigint(0) NOT NULL COMMENT '当前任务完成时间',
  `finish_group` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '已完成任务组',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '玩家次元之门表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_task_info
-- ----------------------------
DROP TABLE IF EXISTS `player_task_info`;
CREATE TABLE `player_task_info`  (
  `player_id` bigint(0) NOT NULL COMMENT '主键 玩家id',
  `task_id` int(0) NOT NULL COMMENT '主键 任务id',
  `task_type` int(0) NOT NULL COMMENT '主键 任务类型',
  `task_status` int(0) NOT NULL COMMENT '任务状态',
  `task_progress` bigint(0) NOT NULL COMMENT '任务进度',
  `task_validator` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '任务验证器',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`player_id`, `task_id`, `task_type`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '玩家任务基础表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_task_main
-- ----------------------------
DROP TABLE IF EXISTS `player_task_main`;
CREATE TABLE `player_task_main`  (
  `id` bigint(0) NOT NULL COMMENT '主键 玩家id',
  `finish_main_task` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '已完成的主线任务id',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '玩家主线任务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_task_rookie
-- ----------------------------
DROP TABLE IF EXISTS `player_task_rookie`;
CREATE TABLE `player_task_rookie`  (
  `id` bigint(0) NOT NULL COMMENT '主键 玩家id',
  `group_id` int(0) NOT NULL COMMENT '当前任务组',
  `task_id` int(0) NOT NULL COMMENT '当前任务id',
  `task_count_down_time` bigint(0) NOT NULL COMMENT '当前任务限时奖励截止时间',
  `task_finish_time` bigint(0) NOT NULL COMMENT '当前任务完成时间',
  `finish_group` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '已完成任务组',
  `finish_task` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '已完成当前任务中中的任务',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '玩家大目标任务' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_tech
-- ----------------------------
DROP TABLE IF EXISTS `player_tech`;
CREATE TABLE `player_tech`  (
  `id` bigint(0) NOT NULL COMMENT '主键 玩家id',
  `tech_detail` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '科技详情信息',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '玩家科技信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_temple_battle
-- ----------------------------
DROP TABLE IF EXISTS `player_temple_battle`;
CREATE TABLE `player_temple_battle`  (
  `player_id` bigint(0) NOT NULL COMMENT '主键 玩家id',
  `battle_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '比赛ID',
  `kill_soldiers` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '击杀士兵',
  `dead_soldiers` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '死亡士兵',
  `bomb_soldiers` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '轰炸士兵',
  `gain_soldiers` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '恢复士兵',
  `occupy_records` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '占领记录',
  `received_tasks` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '已领取的任务奖励',
  `received_targets` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '已领取的目标奖励',
  `received_ranks` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '已领取的排行奖励',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`player_id`, `battle_id`) USING BTREE,
  INDEX `idx_battle_id`(`battle_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '玩家神殿争夺数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_totem
-- ----------------------------
DROP TABLE IF EXISTS `player_totem`;
CREATE TABLE `player_totem`  (
  `id` bigint(0) NOT NULL COMMENT '主键',
  `totem_slot` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '图腾数据',
  `totem_info` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '图腾背包数据',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '玩家图腾数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_trial_tower
-- ----------------------------
DROP TABLE IF EXISTS `player_trial_tower`;
CREATE TABLE `player_trial_tower`  (
  `player_id` bigint(0) NOT NULL COMMENT '主键 玩家id',
  `tower_id` int(0) NOT NULL COMMENT '塔id',
  `layer` int(0) NULL DEFAULT NULL COMMENT '进度层数',
  `reward_status` tinyint(0) NULL DEFAULT NULL COMMENT '结算奖励状态 0-无计算 1-奖励未领取 2-结算奖励已领取',
  `lost_army` bigint(0) NULL DEFAULT NULL COMMENT '当前进度层战斗损兵数量',
  `total_lost_army` bigint(0) NULL DEFAULT NULL COMMENT '战斗总损兵数量',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`player_id`, `tower_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '玩家试炼之塔信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_trigger_gift
-- ----------------------------
DROP TABLE IF EXISTS `player_trigger_gift`;
CREATE TABLE `player_trigger_gift`  (
  `player_id` bigint(0) NOT NULL COMMENT '玩家ID',
  `battle_hurt_compensation` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '战斗死兵补偿触发礼包',
  `excluded_trigger_gift` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '周期内排除的触发礼包',
  `trigger_gift_frequency` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '周期内触发过的次数',
  `triggered_gifts` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '当前已经触发的礼包',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`player_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '玩家触发礼包' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_tunnel
-- ----------------------------
DROP TABLE IF EXISTS `player_tunnel`;
CREATE TABLE `player_tunnel`  (
  `player_id` bigint(0) NOT NULL COMMENT '主键 玩家id',
  `player_data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '玩家数据',
  `visa_data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '跨服数据',
  `state` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '跨服状态',
  `source_server_type_id` int(0) NOT NULL COMMENT '来源服务器类型',
  `source_server_id` int(0) NOT NULL COMMENT '来源服务器ID',
  `target_server_type_id` int(0) NOT NULL COMMENT '目标服务器类型',
  `target_server_id` int(0) NOT NULL COMMENT '目标服务器ID',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`player_id`) USING BTREE,
  INDEX `idx_state`(`state`) USING BTREE,
  INDEX `idx_source_server`(`source_server_type_id`, `source_server_id`, `state`) USING BTREE,
  INDEX `idx_target_server`(`target_server_type_id`, `target_server_id`, `state`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '玩家跨服表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_ultimate_challenge
-- ----------------------------
DROP TABLE IF EXISTS `player_ultimate_challenge`;
CREATE TABLE `player_ultimate_challenge`  (
  `id` bigint(0) NOT NULL COMMENT '玩家id',
  `pass_count` int(0) NOT NULL COMMENT '击破的关卡数',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '玩家极限挑战表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_union
-- ----------------------------
DROP TABLE IF EXISTS `player_union`;
CREATE TABLE `player_union`  (
  `player_id` bigint(0) NOT NULL DEFAULT 0 COMMENT '玩家ID',
  `first_join` tinyint(0) NOT NULL DEFAULT 1 COMMENT '是否是首次加入',
  `diamond_donate_times` int(0) NOT NULL DEFAULT 0 COMMENT '钻石捐献次数',
  `donate_times` int(0) NOT NULL DEFAULT 0 COMMENT '剩余捐献次数',
  `donate_refresh_time` bigint(0) NOT NULL DEFAULT 0 COMMENT '捐献次数刷新时间',
  `daily_union_coin` bigint(0) NOT NULL DEFAULT 0 COMMENT '每日联盟币',
  `gift_anonymous` tinyint(0) NOT NULL DEFAULT 0 COMMENT '匿名购买礼物',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`player_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '玩家联盟数据' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_vip
-- ----------------------------
DROP TABLE IF EXISTS `player_vip`;
CREATE TABLE `player_vip`  (
  `id` bigint(0) NOT NULL COMMENT '主键 玩家id',
  `vip_level` int(0) NULL DEFAULT 1 COMMENT 'vip等级',
  `vip_exp` int(0) NULL DEFAULT 0 COMMENT 'vip经验',
  `login_days` int(0) NULL DEFAULT 0 COMMENT '连续登录天数',
  `vip_login_time` bigint(0) NULL DEFAULT 0 COMMENT 'vip登录时间即领奖时间',
  `is_reward` bit(1) NULL DEFAULT NULL,
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '玩家vip信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for player_welfare
-- ----------------------------
DROP TABLE IF EXISTS `player_welfare`;
CREATE TABLE `player_welfare`  (
  `player_id` bigint(0) NOT NULL COMMENT '玩家ID',
  `welfare` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '福利中心活动',
  `frequency` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '福利中心活动次数',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`player_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '玩家福利中心' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for switch_control
-- ----------------------------
DROP TABLE IF EXISTS `switch_control`;
CREATE TABLE `switch_control`  (
  `id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配置ID',
  `switch_data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配置信息',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '开关表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ultimate_challenge
-- ----------------------------
DROP TABLE IF EXISTS `ultimate_challenge`;
CREATE TABLE `ultimate_challenge`  (
  `id` int(0) NOT NULL COMMENT '关卡id',
  `pass_records` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '击破记录列表,三条击破记录,第一条为首破记录,二三条为最近击破记录,JSON格式',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '极限挑战表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for world_march
-- ----------------------------
DROP TABLE IF EXISTS `world_march`;
CREATE TABLE `world_march`  (
  `id` bigint(0) NOT NULL COMMENT '主键',
  `type` int(0) NOT NULL COMMENT '行军类型',
  `state` int(0) NOT NULL COMMENT '行军状态',
  `owner_id` bigint(0) NOT NULL COMMENT '拥有者ID',
  `march_data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '行军数据',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_type`(`type`) USING BTREE,
  INDEX `idx_state`(`state`) USING BTREE,
  INDEX `idx_owner_id`(`owner_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '世界行军表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for world_point
-- ----------------------------
DROP TABLE IF EXISTS `world_point`;
CREATE TABLE `world_point`  (
  `id` int(0) NOT NULL COMMENT '主键,(x << 16) | (y & 0xFFFF)',
  `type` int(0) NOT NULL DEFAULT 0 COMMENT '地格类型',
  `x` int(0) NOT NULL DEFAULT 0,
  `y` int(0) NOT NULL DEFAULT 0,
  `main_x` int(0) NOT NULL DEFAULT 0 COMMENT '主x',
  `main_y` int(0) NOT NULL DEFAULT 0 COMMENT '主y',
  `extra_data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '额外信息,比如资源点信息,怪物血量,怪物巢穴等',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_type`(`type`) USING BTREE,
  INDEX `idx_x_y`(`x`, `y`) USING BTREE,
  INDEX `idx_mx_my`(`main_x`, `main_y`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '世界地图表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for world_temple_active_player
-- ----------------------------
DROP TABLE IF EXISTS `world_temple_active_player`;
CREATE TABLE `world_temple_active_player`  (
  `player_id` bigint(0) NOT NULL COMMENT '玩家ID',
  `battle_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '比赛ID',
  `score` bigint(0) NOT NULL COMMENT '玩家分数',
  `activity_score` bigint(0) NOT NULL COMMENT '玩家活动分数',
  `combat_loss` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '玩家士兵死亡',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`player_id`, `battle_id`) USING BTREE,
  INDEX `idx_battle_id`(`battle_id`) USING BTREE,
  INDEX `idx_player_id`(`player_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '神殿争夺活跃玩家表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for world_temple_battle_building
-- ----------------------------
DROP TABLE IF EXISTS `world_temple_battle_building`;
CREATE TABLE `world_temple_battle_building`  (
  `id` int(0) NOT NULL COMMENT '建筑ID',
  `occupied_start_time` bigint(0) NOT NULL COMMENT '占领开始时间',
  `occupied_next_tower_bombard_time` bigint(0) NOT NULL COMMENT '下次哨塔轰击时间',
  `occupied_super_garrison_end_time` bigint(0) NOT NULL COMMENT '超级驻防结束时间',
  `occupied_union_bean` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '占领联盟',
  `occupied_player_bean` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '占领玩家',
  `occupied_durations` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '占领时长记录',
  `occupied_records` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '占领战争记录',
  `occupied_vars` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '占领变量记录',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '神殿争夺建筑表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for world_temple_battle_decree
-- ----------------------------
DROP TABLE IF EXISTS `world_temple_battle_decree`;
CREATE TABLE `world_temple_battle_decree`  (
  `id` int(0) NOT NULL COMMENT '政令ID',
  `battle_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '比赛ID',
  `use_time` bigint(0) NOT NULL COMMENT '使用时间',
  `use_count` int(0) NOT NULL COMMENT '使用次数',
  `reset_time` bigint(0) NOT NULL COMMENT '重置时间',
  `expire_time` bigint(0) NOT NULL COMMENT '失效时间',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '神殿争夺政令表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for world_temple_battle_decree_log
-- ----------------------------
DROP TABLE IF EXISTS `world_temple_battle_decree_log`;
CREATE TABLE `world_temple_battle_decree_log`  (
  `id` bigint(0) NOT NULL COMMENT '唯一id',
  `battle_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '比赛ID',
  `decree_id` int(0) NOT NULL COMMENT '政令ID',
  `use_time` bigint(0) NOT NULL COMMENT '使用时间',
  `union_bean` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '使用联盟',
  `player_bean` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '使用玩家',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_battle_id`(`battle_id`) USING BTREE,
  INDEX `idx_decree_id`(`decree_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '神殿争夺政令日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for world_temple_battle_grant_log
-- ----------------------------
DROP TABLE IF EXISTS `world_temple_battle_grant_log`;
CREATE TABLE `world_temple_battle_grant_log`  (
  `id` bigint(0) NOT NULL COMMENT '唯一id',
  `battle_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '比赛ID',
  `title_id` int(0) NOT NULL COMMENT '头衔ID',
  `grant_time` bigint(0) NOT NULL COMMENT '分配时间',
  `grant_resource` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分配资源',
  `send_union_bean` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '发送联盟',
  `send_player_bean` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '发送玩家',
  `receive_union_bean` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '接收联盟',
  `receive_player_bean` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '接收玩家',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_battle_id`(`battle_id`) USING BTREE,
  INDEX `idx_title_id`(`title_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '神殿争夺国库分配日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for world_temple_battle_pantheon
-- ----------------------------
DROP TABLE IF EXISTS `world_temple_battle_pantheon`;
CREATE TABLE `world_temple_battle_pantheon`  (
  `id` int(0) NOT NULL COMMENT '就任名次',
  `king` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '国王信息',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '神殿争夺名人堂' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for world_temple_battle_reward
-- ----------------------------
DROP TABLE IF EXISTS `world_temple_battle_reward`;
CREATE TABLE `world_temple_battle_reward`  (
  `battle_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '比赛ID',
  `reward_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '奖励剩余数量',
  `reward_record` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '奖励领取记录',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`battle_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '神殿争夺赏赐表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for world_temple_battle_reward_log
-- ----------------------------
DROP TABLE IF EXISTS `world_temple_battle_reward_log`;
CREATE TABLE `world_temple_battle_reward_log`  (
  `id` bigint(0) NOT NULL COMMENT '唯一id',
  `battle_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '比赛ID',
  `reward_id` int(0) NOT NULL COMMENT '赏赐ID',
  `reward_time` bigint(0) NOT NULL COMMENT '赏赐时间',
  `send_union_bean` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '发送联盟',
  `send_player_bean` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '发送玩家',
  `receive_union_bean` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '接收联盟',
  `receive_player_bean` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '接收玩家',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_battle_id`(`battle_id`) USING BTREE,
  INDEX `idx_reward_id`(`reward_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '神殿争夺赏赐日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for world_temple_battle_tax_log
-- ----------------------------
DROP TABLE IF EXISTS `world_temple_battle_tax_log`;
CREATE TABLE `world_temple_battle_tax_log`  (
  `id` bigint(0) NOT NULL COMMENT '唯一id',
  `server_id` int(0) NOT NULL COMMENT '服务器ID',
  `kingdom_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '王国名称',
  `tax_time` bigint(0) NOT NULL COMMENT '税收时间',
  `tax_resource` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '税收资源',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_server_id`(`server_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '神殿争夺国库税收日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for world_temple_battle_title
-- ----------------------------
DROP TABLE IF EXISTS `world_temple_battle_title`;
CREATE TABLE `world_temple_battle_title`  (
  `id` int(0) NOT NULL COMMENT '头衔ID',
  `appoint_time` bigint(0) NOT NULL COMMENT '任命时间',
  `player_id` bigint(0) NOT NULL COMMENT '任命玩家',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '神殿争夺头衔表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for world_temple_battle_title_log
-- ----------------------------
DROP TABLE IF EXISTS `world_temple_battle_title_log`;
CREATE TABLE `world_temple_battle_title_log`  (
  `id` bigint(0) NOT NULL COMMENT '唯一id',
  `battle_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '比赛ID',
  `title_id` int(0) NOT NULL COMMENT '头衔ID',
  `appoint_time` bigint(0) NOT NULL COMMENT '任命时间',
  `appoint_union_bean` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任命联盟',
  `appoint_player_bean` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任命玩家',
  `operate_union_bean` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作联盟',
  `operate_player_bean` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作玩家',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_battle_id`(`battle_id`) USING BTREE,
  INDEX `idx_title_id`(`title_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '神殿争夺头衔日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for world_var
-- ----------------------------
DROP TABLE IF EXISTS `world_var`;
CREATE TABLE `world_var`  (
  `key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `value` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`key`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '世界变量表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
