/*
 Navicat Premium Data Transfer

 Source Server         : k2_wifi
 Source Server Type    : MySQL
 Source Server Version : 80028
 Source Host           : 172.16.20.121:3306
 Source Schema         : magics-global

 Target Server Type    : MySQL
 Target Server Version : 80028
 File Encoding         : 65001

 Date: 13/11/2023 14:41:16
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for block_ip
-- ----------------------------
DROP TABLE IF EXISTS `block_ip`;
CREATE TABLE `block_ip`  (
  `block_ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '封禁的ip地址',
  `relieve_time` bigint(0) NOT NULL COMMENT '封禁结束时间',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`block_ip`) USING BTREE,
  INDEX `idx_relieve_time`(`relieve_time`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '封禁IP' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `user_role_reg`;
CREATE TABLE `user_role_reg`  (
  `game_uid` bigint(0) NOT NULL COMMENT '角色ID',
  `account_uid` bigint(0) NOT NULL COMMENT '账号ID',
  `account` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '平台账号',
  `device` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '设备的唯一标识',
  `app_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `app_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `app_version` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `app_package_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `channel` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `platform` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `country` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `language` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `network` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `phone_model` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `device_info` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `device_os` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `os_version` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `apps_flyer_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `advertising_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`game_uid`) USING BTREE,
  INDEX `idx_account_uid`(`account_uid`) USING BTREE,
  INDEX `idx_account`(`account`) USING BTREE,
  INDEX `idx_device`(`device`) USING BTREE,
  INDEX `idx_app_id`(`app_id`) USING BTREE,
  INDEX `idx_app_name`(`app_name`) USING BTREE,
  INDEX `idx_app_version`(`app_version`) USING BTREE,
  INDEX `idx_app_package_name`(`app_package_name`) USING BTREE,
  INDEX `idx_channel`(`channel`) USING BTREE,
  INDEX `idx_platform`(`platform`) USING BTREE,
  INDEX `idx_country`(`country`) USING BTREE,
  INDEX `idx_language`(`language`) USING BTREE,
  INDEX `idx_ip`(`ip`) USING BTREE,
  INDEX `idx_network`(`network`) USING BTREE,
  INDEX `idx_phone_model`(`phone_model`) USING BTREE,
  INDEX `idx_device_info`(`device_info`) USING BTREE,
  INDEX `idx_device_os`(`device_os`) USING BTREE,
  INDEX `idx_os_version`(`os_version`) USING BTREE,
  INDEX `idx_apps_flyer_id`(`apps_flyer_id`) USING BTREE,
  INDEX `idx_advertising_id`(`advertising_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色注册信息表' ROW_FORMAT = Dynamic;
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`  (
  `uid` bigint(0) NOT NULL COMMENT '角色ID',
  `account_uid` bigint(0) NOT NULL COMMENT '账号ID',
  `server_id` int(0) NOT NULL COMMENT '服务器ID',
  `ban_flag` int(0) NOT NULL DEFAULT 0 COMMENT '是否被封禁标志',
  `last_login_time` bigint(0) NULL DEFAULT NULL COMMENT '最后登录时间(UTC timestamp)',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `idx_account_uid`(`account_uid`, `last_login_time`) USING BTREE,
  INDEX `idx_account_uid_server_id`(`account_uid`, `server_id`, `last_login_time`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;
DROP TABLE IF EXISTS `user_account_reg`;
CREATE TABLE `user_account_reg`  (
  `account_uid` bigint(0) NOT NULL COMMENT '账号ID',
  `account` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '平台账号',
  `device` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '设备的唯一标识',
  `app_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `app_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `app_version` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `app_package_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `channel` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `platform` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `country` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `language` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `network` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `phone_model` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `device_info` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `device_os` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `os_version` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `apps_flyer_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `advertising_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`account_uid`) USING BTREE,
  INDEX `idx_account`(`account`) USING BTREE,
  INDEX `idx_device`(`device`) USING BTREE,
  INDEX `idx_app_id`(`app_id`) USING BTREE,
  INDEX `idx_app_name`(`app_name`) USING BTREE,
  INDEX `idx_app_version`(`app_version`) USING BTREE,
  INDEX `idx_app_package_name`(`app_package_name`) USING BTREE,
  INDEX `idx_channel`(`channel`) USING BTREE,
  INDEX `idx_platform`(`platform`) USING BTREE,
  INDEX `idx_country`(`country`) USING BTREE,
  INDEX `idx_language`(`language`) USING BTREE,
  INDEX `idx_ip`(`ip`) USING BTREE,
  INDEX `idx_network`(`network`) USING BTREE,
  INDEX `idx_phone_model`(`phone_model`) USING BTREE,
  INDEX `idx_device_info`(`device_info`) USING BTREE,
  INDEX `idx_device_os`(`device_os`) USING BTREE,
  INDEX `idx_os_version`(`os_version`) USING BTREE,
  INDEX `idx_apps_flyer_id`(`apps_flyer_id`) USING BTREE,
  INDEX `idx_advertising_id`(`advertising_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '账号注册信息表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `user_account_login_log`;
CREATE TABLE `user_account_login_log`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `account_uid` bigint(0) NOT NULL COMMENT '账号ID',
  `account` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '平台账号',
  `device` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '设备的唯一标识',
  `app_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `app_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `app_version` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `app_package_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `channel` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `platform` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `country` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `language` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `network` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `phone_model` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `device_info` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `device_os` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `os_version` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `apps_flyer_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `advertising_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_account`(`account`) USING BTREE,
  INDEX `idx_device`(`device`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1091 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '账号登录日志表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `user_account`;
CREATE TABLE `user_account`  (
  `uid` bigint(0) NOT NULL COMMENT '账号ID',
  `account` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '平台账号',
  `device` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '设备的唯一标识',
  `gm_flag` int(0) NOT NULL DEFAULT 0 COMMENT '是否GM标志',
  `ban_flag` int(0) NOT NULL DEFAULT 0 COMMENT '是否被封禁标志',
  `guide_policy` int(0) NULL DEFAULT 0 COMMENT '新手引导策略, 0=不能跳过, 1=可以跳过',
  `account_bind` int(0) NOT NULL DEFAULT 0 COMMENT '绑定状态, 0=未绑定, 1=已绑定',
  `facebook_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'facebook id',
  `google_play_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'google play id',
  `game_center_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'game center id',
  `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '账号绑定的email',
  `app_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `app_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `app_version` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `app_package_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `channel` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `platform` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `country` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `language` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `network` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `phone_model` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `device_info` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `device_os` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `os_version` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `apps_flyer_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `advertising_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`uid`) USING BTREE,
  UNIQUE INDEX `idx_account`(`account`) USING BTREE,
  INDEX `idx_device`(`device`) USING BTREE,
  INDEX `idx_account_bind`(`account_bind`) USING BTREE,
  INDEX `idx_facebook_id`(`facebook_id`) USING BTREE,
  INDEX `idx_google_play_id`(`google_play_id`) USING BTREE,
  INDEX `idx_game_center_id`(`game_center_id`) USING BTREE,
  INDEX `idx_email`(`email`) USING BTREE,
  INDEX `idx_app_id`(`app_id`) USING BTREE,
  INDEX `idx_app_name`(`app_name`) USING BTREE,
  INDEX `idx_app_version`(`app_version`) USING BTREE,
  INDEX `idx_app_package_name`(`app_package_name`) USING BTREE,
  INDEX `idx_channel`(`channel`) USING BTREE,
  INDEX `idx_platform`(`platform`) USING BTREE,
  INDEX `idx_country`(`country`) USING BTREE,
  INDEX `idx_language`(`language`) USING BTREE,
  INDEX `idx_ip`(`ip`) USING BTREE,
  INDEX `idx_network`(`network`) USING BTREE,
  INDEX `idx_phone_model`(`phone_model`) USING BTREE,
  INDEX `idx_device_info`(`device_info`) USING BTREE,
  INDEX `idx_device_os`(`device_os`) USING BTREE,
  INDEX `idx_os_version`(`os_version`) USING BTREE,
  INDEX `idx_apps_flyer_id`(`apps_flyer_id`) USING BTREE,
  INDEX `idx_advertising_id`(`advertising_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '账号表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `ticket`;
CREATE TABLE `ticket`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `stub` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_stub`(`stub`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10320 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '唯一ID生成表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `server`;
CREATE TABLE `server`  (
  `type_id` int(0) NOT NULL COMMENT '服务器类型',
  `server_id` int(0) NOT NULL COMMENT '服务器ID',
  `type_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '服务器类型名称',
  `server_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '服务器名称',
  `host` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '服务器地址',
  `port` int(0) NOT NULL COMMENT '服务器端口',
  `http_port` int(0) NOT NULL COMMENT 'HTTP端口',
  `rpc_port` int(0) NOT NULL COMMENT 'RPC端口',
  `open_time` datetime(0) NOT NULL COMMENT '开服时间',
  `target_server_id` int(0) NOT NULL DEFAULT 0 COMMENT '合服后的目标服',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '服务器备注',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`type_id`, `server_id`) USING BTREE,
  INDEX `idx_server_id`(`server_id`) USING BTREE,
  INDEX `idx_type_name`(`type_name`) USING BTREE,
  INDEX `idx_host`(`host`) USING BTREE,
  INDEX `idx_port`(`port`) USING BTREE,
  INDEX `idx_open_time`(`open_time`) USING BTREE,
  INDEX `idx_target_server_id`(`target_server_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '服务器表' ROW_FORMAT = Dynamic;
DROP TABLE IF EXISTS `game_server`;
CREATE TABLE `game_server`  (
  `server_id` int(0) NOT NULL COMMENT '服务器ID',
  `server_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '服务器名称',
  `host` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '服务器地址',
  `port` int(0) NOT NULL COMMENT '服务器端口',
  `http_port` int(0) NOT NULL COMMENT 'HTTP端口',
  `rpc_port` int(0) NOT NULL COMMENT 'RPC端口',
  `open_time` datetime(0) NOT NULL COMMENT '开服时间',
  `target_server_id` int(0) NOT NULL DEFAULT 0 COMMENT '合服后的目标服',
  `union_server_id` int(0) NOT NULL COMMENT '联盟服ID',
  `game_version` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '游戏内容,策划,服务器,客户端所有配置及代码的统一版本号',
  `maintain_end_time` datetime(0) NULL DEFAULT NULL COMMENT '维护结束时间',
  `maintain_notice` bigint(0) NULL DEFAULT NULL COMMENT '维护公告多语言Id',
  `total_max_count` int(0) NOT NULL DEFAULT 99999 COMMENT '最大注册数,0为不限制',
  `day_max_count` int(0) NOT NULL DEFAULT 20000 COMMENT '当日最大注册数,0为不限制',
  `hour_max_count` int(0) NOT NULL DEFAULT 10000 COMMENT '小时最大注册数,0为不限制',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`server_id`) USING BTREE,
  INDEX `idx_server_name`(`server_name`) USING BTREE,
  INDEX `idx_host`(`host`) USING BTREE,
  INDEX `idx_open_time`(`open_time`) USING BTREE,
  INDEX `idx_union_server_id`(`union_server_id`) USING BTREE,
  INDEX `idx_target_server_id`(`target_server_id`) USING BTREE,
  INDEX `idx_game_version`(`game_version`) USING BTREE,
  INDEX `idx_maintain_end_time`(`maintain_end_time`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '服务器信息表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `register_group`;
CREATE TABLE `register_group`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分组配置名称',
  `inclusive_country_iso_codes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '包含的国家,逗号(,)分隔',
  `exclusive_country_iso_codes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '排除的国家,逗号(,)分隔',
  `inclusive_languages` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '包含的语言,逗号(,)分隔',
  `exclusive_languages` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '排除的语言,逗号(,)分隔',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_name`(`name`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '导量策略-分组配置表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `register_policy_group`;
CREATE TABLE `register_policy_group`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `policy_id` int(0) NOT NULL COMMENT '策略ID',
  `group_id` int(0) NOT NULL COMMENT '分组ID',
  `ratio` int(0) NOT NULL DEFAULT 0 COMMENT '所在分组内的占比',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_policy_id`(`policy_id`) USING BTREE,
  INDEX `idx_group_id`(`group_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '导量策略方案表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `register_server_policy`;
CREATE TABLE `register_server_policy`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `policy_id` int(0) NOT NULL COMMENT '策略ID',
  `server_id` int(0) NOT NULL COMMENT '服务器ID',
  `priority` int(0) NOT NULL DEFAULT 0 COMMENT '优先级,越小越优先,相同优先级则随机',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_policy_id`(`policy_id`) USING BTREE,
  INDEX `idx_server_id`(`server_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '导量服务器应用表' ROW_FORMAT = Dynamic;
DROP TABLE IF EXISTS `register_group_locale`;
CREATE TABLE `register_group_locale`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `group_id` int(0) NOT NULL COMMENT '分组ID',
  `locale_id` int(0) NOT NULL COMMENT '地区ID',
  `ratio` int(0) NOT NULL DEFAULT 0 COMMENT '所在分组内的占比',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_group_id`(`group_id`) USING BTREE,
  INDEX `idx_locale_id`(`locale_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '导量策略-分组方案表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `register_locale`;
CREATE TABLE `register_locale`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '地区配置名称',
  `inclusive_country_iso_codes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '包含的国家,逗号(,)分隔',
  `exclusive_country_iso_codes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '排除的国家,逗号(,)分隔',
  `inclusive_languages` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '包含的语言,逗号(,)分隔',
  `exclusive_languages` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '排除的语言,逗号(,)分隔',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_name`(`name`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '导量策略-地区配置表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `register_policy`;
CREATE TABLE `register_policy`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '注册策略配置名称',
  `priority` int(0) NOT NULL DEFAULT 0 COMMENT '优先级,越小越优先,相同优先级则随机',
  `disable` int(0) NOT NULL DEFAULT 0 COMMENT '0: 启用 1: 禁用',
  `total_max_count` int(0) NOT NULL DEFAULT 0 COMMENT '最大注册数,0为不限制',
  `day_max_count` int(0) NOT NULL DEFAULT 0 COMMENT '当日最大注册数,0为不限制',
  `hour_max_count` int(0) NOT NULL DEFAULT 0 COMMENT '小时最大注册数,0为不限制',
  `inclusive_country_iso_codes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '包含的国家,逗号(,)分隔',
  `exclusive_country_iso_codes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '排除的国家,逗号(,)分隔',
  `inclusive_languages` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '包含的语言,逗号(,)分隔',
  `exclusive_languages` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '排除的语言,逗号(,)分隔',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_name`(`name`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '导量策略表' ROW_FORMAT = Dynamic;
SET FOREIGN_KEY_CHECKS = 1;
