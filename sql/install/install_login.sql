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

 Date: 16/11/2023 10:09:03
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for advertising
-- ----------------------------
DROP TABLE IF EXISTS `advertising`;
CREATE TABLE `advertising`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `report_date` datetime(0) NOT NULL COMMENT '报告日期',
  `game_ename` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '游戏代号',
  `campaign_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '广告计划ID',
  `campaign_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '广告计划名称',
  `adset_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '广告组ID 【facebook 独有】',
  `adset_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '广告组名称【facebook 独有】',
  `ad_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '广告ID【facebook 独有】',
  `ad_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '广告名称【facebook 独有】',
  `clicks` int(0) NOT NULL DEFAULT 0 COMMENT '点击',
  `impressions` int(0) NOT NULL DEFAULT 0 COMMENT '展示',
  `spend` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '广告花费, 币种美金',
  `installs` int(0) NOT NULL DEFAULT 0 COMMENT '安装',
  `os` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '系统（ios 或者 android）',
  `country` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '国家（简称, 2位大写字母）',
  `platform` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '二级渠道(例如: Messenger、Instagram等)【facebook 独有】',
  `channel` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '媒体概念：例如 facebook、google',
  `req_time` bigint(0) NOT NULL COMMENT '数据推送时间戳(秒)',
  `account_count` bigint(0) NULL DEFAULT 0 COMMENT '报表汇总数据：账号总数',
  `login1` bigint(0) NULL DEFAULT 0 COMMENT '报表汇总数据：次日登录总数',
  `login3` bigint(0) NULL DEFAULT 0 COMMENT '报表汇总数据：3日登录总数',
  `login7` bigint(0) NULL DEFAULT NULL COMMENT '报表汇总数据：7日登录总数',
  `pay_count0` bigint(0) NULL DEFAULT 0 COMMENT '报表汇总数据：首日充值订单总数',
  `money0` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '报表汇总数据：首日充值总额',
  `pay_count3` bigint(0) NULL DEFAULT 0 COMMENT '报表汇总数据：3日充值订单总数',
  `money3` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '报表汇总数据：3日充值总额',
  `pay_count7` bigint(0) NULL DEFAULT 0 COMMENT '报表汇总数据：7日充值订单总数',
  `money7` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '报表汇总数据：7日充值总额',
  `pay_count` bigint(0) NULL DEFAULT 0 COMMENT '报表汇总数据：所有充值订单总数',
  `money` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '报表汇总数据：所有充值总额',
  `data_l1` bigint(0) NULL DEFAULT 0 COMMENT '报表汇总数据：预留long类型数据列1',
  `data_l2` bigint(0) NULL DEFAULT 0 COMMENT '报表汇总数据：预留long类型数据列2',
  `data_l3` bigint(0) NULL DEFAULT 0 COMMENT '报表汇总数据：预留long类型数据列3',
  `data_d1` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '报表汇总数据：预留double类型数据列1',
  `data_d2` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '报表汇总数据：预留double类型数据列2',
  `data_d3` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '报表汇总数据：预留double类型数据列3',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_unique`(`report_date`, `game_ename`, `campaign_id`, `adset_id`, `ad_id`, `ad_name`, `os`, `country`) USING BTREE,
  INDEX `idx_report_date`(`report_date`) USING BTREE,
  INDEX `idx_campaign_id`(`campaign_id`) USING BTREE,
  INDEX `idx_campaign_name`(`campaign_name`) USING BTREE,
  INDEX `idx_adset_id`(`adset_id`) USING BTREE,
  INDEX `idx_adset_name`(`adset_name`) USING BTREE,
  INDEX `idx_ad_id`(`ad_id`) USING BTREE,
  INDEX `idx_ad_name`(`ad_name`) USING BTREE,
  INDEX `idx_os`(`os`) USING BTREE,
  INDEX `idx_country`(`country`) USING BTREE,
  INDEX `idx_channel`(`channel`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '广告数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for advertising_account_login
-- ----------------------------
DROP TABLE IF EXISTS `advertising_account_login`;
CREATE TABLE `advertising_account_login`  (
  `account` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '平台账号id',
  `login_day` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '第几天登录',
  `amount` int(0) NOT NULL COMMENT '登录次数',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`account`, `login_day`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '账号与登录天数关系表，用于计算账号留存数' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for advertising_account_pay
-- ----------------------------
DROP TABLE IF EXISTS `advertising_account_pay`;
CREATE TABLE `advertising_account_pay`  (
  `account` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '平台账号id',
  `pay_day` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '第几天支付',
  `amount` int(0) NOT NULL COMMENT '支付次数',
  `total` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '支付总额',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`account`, `pay_day`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '账号与支付天数关系表，用于计算账号ROI和付费率' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for advertising_account2ad
-- ----------------------------
DROP TABLE IF EXISTS `advertising_account2ad`;
CREATE TABLE `advertising_account2ad`  (
  `account` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '平台账号id',
  `campaign_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'campaign_id',
  `adset_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '广告组ID 【facebook 独有】',
  `ad_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '广告ID【facebook 独有】',
  `to_days` int(0) NOT NULL COMMENT '账号创建偏移天数，MySQL的to_day函数，用于天数聚合',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`account`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '平台账号id与广告信息聚合结果表，用于广告数据表与账号的聚合' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for advertising_afid
-- ----------------------------
DROP TABLE IF EXISTS `advertising_afid`;
CREATE TABLE `advertising_afid`  (
  `appsflyer_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'appsflyer_id',
  `campaign_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'campaign_id',
  `adset_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '广告组ID 【facebook 独有】',
  `ad_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '广告ID【facebook 独有】',
  `amount` int(0) NOT NULL COMMENT '聚合总数',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`appsflyer_id`, `campaign_id`, `adset_id`, `ad_id`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'appsflyer id聚合结果表，afid与广告之间的关系，用于与user account聚合成为account2ad关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for advertising_afid_wrong
-- ----------------------------
DROP TABLE IF EXISTS `advertising_afid_wrong`;
CREATE TABLE `advertising_afid_wrong`  (
  `appsflyer_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'appsflyer_id',
  `campaign_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'campaign_id',
  `amount` int(0) NOT NULL COMMENT '聚合总数',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`appsflyer_id`, `campaign_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'appsflyer id可能存在问题的列表，一般是同广告id下有不同的广告分组、广告id，用于聚合成表advertising_afid的时候排除这些afid' ROW_FORMAT = Dynamic;

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

-- ----------------------------
-- Table structure for cd_key_info
-- ----------------------------
DROP TABLE IF EXISTS `cd_key_info`;
CREATE TABLE `cd_key_info`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `gift_id` bigint(0) NOT NULL COMMENT '礼包id',
  `batch_id` int(0) NOT NULL COMMENT '批次id',
  `detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'cdKey记录',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_gift_id`(`gift_id`) USING BTREE,
  INDEX `idx_batch_id`(`batch_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '礼包批次记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for cd_key_receive
-- ----------------------------
DROP TABLE IF EXISTS `cd_key_receive`;
CREATE TABLE `cd_key_receive`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `gift_id` bigint(0) NOT NULL COMMENT '礼包id',
  `cd_key` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '兑换码',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '礼包名称',
  `type` int(0) NULL DEFAULT NULL COMMENT '礼包类型',
  `server_id` int(0) NULL DEFAULT NULL COMMENT '服务器',
  `player_id` bigint(0) NULL DEFAULT NULL COMMENT '玩家id',
  `player_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '玩家名称',
  `platform` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '平台',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_gift_id`(`gift_id`) USING BTREE,
  INDEX `idx_player_id`(`player_id`) USING BTREE,
  INDEX `idx_cd_key_id`(`cd_key`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '礼包领奖记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for chat_report
-- ----------------------------
DROP TABLE IF EXISTS `chat_report`;
CREATE TABLE `chat_report`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `reporter_uid` bigint(0) NOT NULL COMMENT '举报人uid',
  `reporter_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '举报人昵称',
  `target_uid` bigint(0) NOT NULL COMMENT '被举报人uid',
  `target_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '被举报人昵称',
  `target_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '被举报的聊天内容',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_reporter_uid`(`reporter_uid`) USING BTREE,
  INDEX `idx_reporter_name`(`reporter_name`) USING BTREE,
  INDEX `idx_target_uid`(`target_uid`) USING BTREE,
  INDEX `idx_target_name`(`target_name`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '聊天举报' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for click_ad_log
-- ----------------------------
DROP TABLE IF EXISTS `click_ad_log`;
CREATE TABLE `click_ad_log`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `appsflyer_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'appsflyer_id',
  `campaign_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'campaign_id',
  `idfa` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'idfa',
  `af_ad` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'af_ad',
  `idfv` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'idfv',
  `imei` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'imei',
  `advertising_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'advertising_id',
  `android_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'android_id',
  `bundle_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'bundle_id',
  `ad_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'ad_id',
  `af_adset` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'af_adset',
  `af_adset_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'af_adset_id',
  `af_c_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'af_c_id',
  `af_channel` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'af_channel',
  `campaign_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'campaign_type',
  `is_retargeting` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'is_retargeting',
  `original_url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'original_url',
  `app_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'app_id',
  `wifi` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'wifi',
  `campaign` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'campaign',
  `country_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'country_code',
  `city` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'city',
  `ip` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'ip',
  `device_model` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'device_model',
  `device_category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'device_category',
  `device_download_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'device_download_time',
  `install_app_store` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'install_app_store',
  `install_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'install_time',
  `is_lat` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'is_lat',
  `language` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'language',
  `app_version` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'app_version',
  `os_version` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'os_version',
  `app_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'app_name',
  `platform` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'platform',
  `store_reinstall` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'store_reinstall',
  `amazon_aid` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'amazon_aid',
  `attributed_touch_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'attributed_touch_type',
  `is_primary_attribution` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'is_primary_attribution',
  `gp_install_begin` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'gp_install_begin',
  `install_hour` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'install_hour',
  `event_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'event_name',
  `media_source` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '获取归因的媒体渠道或restricted(受限渠道)',
  `data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '回传的所有参数json',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_unique`(`appsflyer_id`, `event_name`) USING BTREE,
  INDEX `idx_idfa`(`idfa`) USING BTREE,
  INDEX `idx_advertising_id`(`advertising_id`) USING BTREE,
  INDEX `idx_af_ad`(`af_ad`) USING BTREE,
  INDEX `idx_ad_id`(`ad_id`) USING BTREE,
  INDEX `idx_af_adset_id`(`af_adset_id`) USING BTREE,
  INDEX `idx_af_c_id`(`af_c_id`) USING BTREE,
  INDEX `idx_af_channel`(`af_channel`) USING BTREE,
  INDEX `idx_app_id`(`app_id`) USING BTREE,
  INDEX `idx_campaign_type`(`campaign_type`) USING BTREE,
  INDEX `idx_country_code`(`country_code`) USING BTREE,
  INDEX `idx_app_version`(`app_version`) USING BTREE,
  INDEX `idx_app_name`(`app_name`) USING BTREE,
  INDEX `idx_gp_install_begin`(`gp_install_begin`) USING BTREE,
  INDEX `idx_is_primary_attribution`(`is_primary_attribution`) USING BTREE,
  INDEX `idx_language`(`language`) USING BTREE,
  INDEX `idx_device_model`(`device_model`) USING BTREE,
  INDEX `idx_install_hour`(`install_hour`) USING BTREE,
  INDEX `idx_install_time`(`install_time`) USING BTREE,
  INDEX `idx_event_name`(`event_name`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '点击广告日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for game_server
-- ----------------------------
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

-- ----------------------------
-- Table structure for gift_pack_log
-- ----------------------------
DROP TABLE IF EXISTS `gift_pack_log`;
CREATE TABLE `gift_pack_log`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `user_id` bigint(0) NOT NULL COMMENT '账号',
  `server_id` int(0) NOT NULL COMMENT 'server_id',
  `order_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'order_id',
  `role_id` bigint(0) NOT NULL COMMENT 'role_id',
  `gift_id` int(0) NOT NULL COMMENT 'gift_id',
  `gift_num` int(0) NOT NULL DEFAULT 0 COMMENT 'gift_num',
  `cmd` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'cmd',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_order_id`(`order_id`) USING BTREE,
  INDEX `idx_role_id`(`role_id`) USING BTREE,
  INDEX `idx_gift_id`(`gift_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'sdk奖励礼包' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for language_text
-- ----------------------------
DROP TABLE IF EXISTS `language_text`;
CREATE TABLE `language_text`  (
  `id` bigint(0) NOT NULL,
  `language` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '语言码(ISO-639-1)',
  `title` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `sub_title` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`, `language`) USING BTREE,
  INDEX `idx_language`(`language`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '多语言配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for language_url
-- ----------------------------
DROP TABLE IF EXISTS `language_url`;
CREATE TABLE `language_url`  (
  `id` bigint(0) NOT NULL,
  `language` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '语言码(ISO-639-1)',
  `url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  INDEX `idx_language`(`language`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '多语言超链接配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for login_notice
-- ----------------------------
DROP TABLE IF EXISTS `login_notice`;
CREATE TABLE `login_notice`  (
  `id` bigint(0) NOT NULL COMMENT '登录公告id,由eyu超级后台生成,每个游戏服唯一',
  `status` int(0) NOT NULL COMMENT '状态',
  `server_id` int(0) NOT NULL COMMENT '游戏服id',
  `language_id` bigint(0) NOT NULL COMMENT 'languageTextId',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`, `server_id`) USING BTREE,
  INDEX `idx_server_id`(`server_id`) USING BTREE,
  INDEX `idx_language_id`(`language_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '登录公告' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for purchase_credit
-- ----------------------------
DROP TABLE IF EXISTS `purchase_credit`;
CREATE TABLE `purchase_credit`  (
  `account` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '玩家账号',
  `money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '购买额度',
  `valid` int(0) NOT NULL DEFAULT 0 COMMENT '是否有效, 0无效, 1有效',
  `expiry_time` datetime(0) NULL DEFAULT NULL COMMENT '过期时间',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`account`) USING BTREE,
  INDEX `idx_account`(`account`, `money`, `valid`, `expiry_time`) USING BTREE COMMENT '玩家账号',
  INDEX `idx_create_time`(`create_time`) USING BTREE COMMENT '创建时间',
  INDEX `idx_update_time`(`update_time`) USING BTREE COMMENT '更新时间'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '购买额度表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for purchase_order
-- ----------------------------
DROP TABLE IF EXISTS `purchase_order`;
CREATE TABLE `purchase_order`  (
  `cp_order` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'CP订单号',
  `sp_order` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'SP订单号',
  `server_id` int(0) NOT NULL COMMENT '玩家服务器',
  `role_id` bigint(0) NOT NULL COMMENT '玩家角色',
  `role_account` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '玩家账号',
  `role_account_uid` bigint(0) NOT NULL COMMENT '玩家账号ID',
  `product_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '充值类型,package,card,coin,subscription',
  `product_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '充值档位',
  `purchase_charge` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '购买商品支付项',
  `purchase_group` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '购买商品组类型',
  `purchase_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '购买商品类型',
  `purchase_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '购买商品ID,一个充值档位对应多个商品',
  `order_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单类型, GM玩家订单, 正式玩家订单等',
  `order_status` int(0) NOT NULL DEFAULT 0 COMMENT '订单状态, 0=开放中,表示订单创建完成后, 没有被调用过发货的状态, 1=成功, 2=失败',
  `currency` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '支付货币',
  `money` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '支付金额',
  `tax` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '支付税额',
  `local_currency` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户支付货币',
  `local_money` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '用户支付金额',
  `real_currency` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '对账货币',
  `real_money` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '对账支付金额',
  `local_to_real_rate` decimal(20, 10) NULL DEFAULT 0.0000000000 COMMENT '用户支付货币转对账货币汇率',
  `coupon_money` decimal(10, 2) NULL DEFAULT NULL COMMENT '对账币种优惠券金额',
  `buy_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  `pay_time` datetime(0) NULL DEFAULT NULL COMMENT '支付时间',
  `pay_type` int(0) NULL DEFAULT 0 COMMENT '支付类型: 1-游戏内购 2-第三方支付',
  `pay_channel` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '支付渠道',
  `expiry_time` datetime(0) NULL DEFAULT NULL COMMENT '订阅过期时间',
  `sandbox` int(0) NULL DEFAULT 0 COMMENT '是否为沙盒环境,0=false,1=true',
  `castle_level` int(0) NULL DEFAULT 0 COMMENT '主城等级',
  `vip_level` int(0) NULL DEFAULT 0 COMMENT 'vip等级',
  `role_level` int(0) NULL DEFAULT 0 COMMENT '角色等级',
  `role_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '角色名称',
  `union_id` int(0) NULL DEFAULT 0 COMMENT '联盟ID',
  `union_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联盟代码',
  `union_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联盟名称',
  `app_version` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '客户端版本号',
  `country` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '国家',
  `lang` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '语言',
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'ip',
  `device_os` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '设备操作系统,iOS,Android,Unknown',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '订单的备注信息,用作扩展使用',
  `detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'SP发货请求的详细数据',
  `is_test` int(0) NULL DEFAULT NULL COMMENT '表示测试订单,0为正常订单',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`cp_order`) USING BTREE,
  INDEX `idx_cp_order`(`cp_order`, `order_status`, `create_time`) USING BTREE COMMENT 'CP订单号',
  INDEX `idx_sp_order`(`sp_order`, `order_status`, `create_time`) USING BTREE COMMENT 'SP订单号',
  INDEX `idx_server_id`(`server_id`, `order_status`, `create_time`) USING BTREE COMMENT '玩家角色服务器',
  INDEX `idx_role_id`(`role_id`, `order_status`, `create_time`) USING BTREE COMMENT '玩家角色',
  INDEX `idx_role_account`(`role_account`, `order_status`, `create_time`) USING BTREE COMMENT '玩家账号',
  INDEX `idx_role_account_uid`(`role_account_uid`, `order_status`, `create_time`) USING BTREE COMMENT '玩家账号ID',
  INDEX `idx_product_type`(`product_type`, `order_status`, `create_time`) USING BTREE COMMENT '充值类型,package,card,coin,subscription',
  INDEX `idx_product_id`(`product_id`, `order_status`, `create_time`) USING BTREE COMMENT '充值档位',
  INDEX `idx_purchase_charge`(`purchase_charge`, `order_status`, `create_time`) USING BTREE COMMENT '购买商品支付项',
  INDEX `idx_purchase_group`(`purchase_group`, `order_status`, `create_time`) USING BTREE COMMENT '购买商品组类型',
  INDEX `idx_purchase_type`(`purchase_type`, `order_status`, `create_time`) USING BTREE COMMENT '购买商品类型',
  INDEX `idx_purchase_id`(`purchase_id`, `order_status`, `create_time`) USING BTREE COMMENT '购买商品ID,一个充值档位对应多个商品',
  INDEX `idx_order_type`(`order_type`, `order_status`, `create_time`) USING BTREE COMMENT '订单类型',
  INDEX `idx_order_status`(`order_status`, `create_time`) USING BTREE COMMENT '订单状态',
  INDEX `idx_currency`(`currency`, `order_status`, `create_time`) USING BTREE COMMENT '支付货币',
  INDEX `idx_money`(`money`, `order_status`, `create_time`) USING BTREE COMMENT '支付金额',
  INDEX `idx_tax`(`tax`, `order_status`, `create_time`) USING BTREE COMMENT '支付税额',
  INDEX `idx_local_currency`(`local_currency`, `order_status`, `create_time`) USING BTREE COMMENT '用户支付货币',
  INDEX `idx_local_money`(`local_money`, `order_status`, `create_time`) USING BTREE COMMENT '用户支付金额',
  INDEX `idx_real_currency`(`real_currency`, `order_status`, `create_time`) USING BTREE COMMENT '对账货币',
  INDEX `idx_real_money`(`real_money`, `order_status`, `create_time`) USING BTREE COMMENT '对账支付金额',
  INDEX `idx_local_to_real_rate`(`local_to_real_rate`, `order_status`, `create_time`) USING BTREE COMMENT '用户支付货币转对账货币汇率',
  INDEX `idx_buy_time`(`buy_time`, `order_status`) USING BTREE COMMENT '下单时间',
  INDEX `idx_pay_time`(`pay_time`, `order_status`) USING BTREE COMMENT '支付时间',
  INDEX `idx_pay_type`(`pay_type`, `order_status`, `create_time`) USING BTREE COMMENT '支付类型: 1-游戏内购 2-第三方支付',
  INDEX `idx_pay_channel`(`pay_channel`, `order_status`, `create_time`) USING BTREE COMMENT '支付渠道',
  INDEX `idx_expiry_time`(`expiry_time`, `order_status`) USING BTREE COMMENT '订阅过期时间',
  INDEX `idx_sandbox`(`sandbox`, `order_status`, `create_time`) USING BTREE COMMENT '沙盒环境',
  INDEX `idx_castle_level`(`castle_level`, `order_status`, `create_time`) USING BTREE COMMENT '主城等级',
  INDEX `idx_vip_level`(`vip_level`, `order_status`, `create_time`) USING BTREE COMMENT 'vip等级',
  INDEX `idx_role_level`(`role_level`, `order_status`, `create_time`) USING BTREE COMMENT '角色等级',
  INDEX `idx_role_name`(`role_name`, `order_status`, `create_time`) USING BTREE COMMENT '角色名称',
  INDEX `idx_union_id`(`union_id`, `order_status`, `create_time`) USING BTREE COMMENT '联盟ID',
  INDEX `idx_union_code`(`union_code`, `order_status`, `create_time`) USING BTREE COMMENT '联盟代码',
  INDEX `idx_union_name`(`union_name`, `order_status`, `create_time`) USING BTREE COMMENT '联盟名称',
  INDEX `idx_app_version`(`app_version`, `order_status`, `create_time`) USING BTREE COMMENT '客户端版本号',
  INDEX `idx_country`(`country`, `order_status`, `create_time`) USING BTREE COMMENT '国家',
  INDEX `idx_lang`(`lang`, `order_status`, `create_time`) USING BTREE COMMENT '语言',
  INDEX `idx_device_os`(`device_os`, `order_status`, `create_time`) USING BTREE COMMENT '设备操作系统',
  INDEX `idx_create_time`(`create_time`) USING BTREE COMMENT '创建时间',
  INDEX `idx_update_time`(`update_time`) USING BTREE COMMENT '更新时间'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '购买订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for purchase_order_log
-- ----------------------------
DROP TABLE IF EXISTS `purchase_order_log`;
CREATE TABLE `purchase_order_log`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键,只在这个表的范围使用,其他地方禁止使用',
  `cp_order` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'CP订单号',
  `sp_order` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'SP订单号',
  `role_id` bigint(0) NULL DEFAULT 0 COMMENT '玩家角色',
  `role_account` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '玩家账号',
  `product_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '充值类型,package,card,coin,subscription',
  `product_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '充值档位',
  `currency` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '支付货币',
  `money` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '支付金额',
  `tax` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '支付税额',
  `local_currency` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户支付货币',
  `local_money` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '用户支付金额',
  `real_currency` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '对账货币',
  `real_money` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '对账支付金额',
  `local_to_real_rate` decimal(20, 10) NULL DEFAULT 0.0000000000 COMMENT '用户支付货币转对账货币汇率',
  `pay_time` datetime(0) NULL DEFAULT NULL COMMENT '支付时间',
  `pay_type` int(0) NULL DEFAULT 0 COMMENT '支付类型: 1-游戏内购 2-第三方支付',
  `pay_channel` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '支付渠道',
  `expiry_time` datetime(0) NULL DEFAULT NULL COMMENT '订阅过期时间',
  `sandbox` int(0) NULL DEFAULT 0 COMMENT '是否为沙盒环境,0=false,1=true',
  `success` int(0) NULL DEFAULT 0 COMMENT '是否发货成功,0=false,1=true',
  `error_code` int(0) NULL DEFAULT NULL COMMENT '错误码',
  `error_message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '错误消息',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '订单的备注信息,用作扩展使用',
  `detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'SP发货请求的详细数据',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_cp_order`(`cp_order`) USING BTREE COMMENT 'CP订单号',
  INDEX `idx_sp_order`(`sp_order`) USING BTREE COMMENT 'SP订单号',
  INDEX `idx_role_id`(`role_id`, `success`, `create_time`) USING BTREE COMMENT '玩家角色',
  INDEX `idx_role_account`(`role_account`, `success`, `create_time`) USING BTREE COMMENT '玩家账号',
  INDEX `idx_product_type`(`product_type`, `success`, `create_time`) USING BTREE COMMENT '充值类型,package,card,coin,subscription',
  INDEX `idx_product_id`(`product_id`, `success`, `create_time`) USING BTREE COMMENT '充值档位',
  INDEX `idx_currency`(`currency`, `success`, `create_time`) USING BTREE COMMENT '支付货币',
  INDEX `idx_money`(`money`, `success`, `create_time`) USING BTREE COMMENT '支付金额',
  INDEX `idx_tax`(`tax`, `success`, `create_time`) USING BTREE COMMENT '支付税额',
  INDEX `idx_local_currency`(`local_currency`, `success`, `create_time`) USING BTREE COMMENT '用户支付货币',
  INDEX `idx_local_money`(`local_money`, `success`, `create_time`) USING BTREE COMMENT '用户支付金额',
  INDEX `idx_real_currency`(`real_currency`, `success`, `create_time`) USING BTREE COMMENT '对账货币',
  INDEX `idx_real_money`(`real_money`, `success`, `create_time`) USING BTREE COMMENT '对账支付金额',
  INDEX `idx_local_to_real_rate`(`local_to_real_rate`, `success`, `create_time`) USING BTREE COMMENT '用户支付货币转对账货币汇率',
  INDEX `idx_pay_time`(`pay_time`, `success`) USING BTREE COMMENT '支付时间',
  INDEX `idx_pay_type`(`pay_type`, `success`, `create_time`) USING BTREE COMMENT '支付时间',
  INDEX `idx_pay_channel`(`pay_channel`, `success`, `create_time`) USING BTREE COMMENT '支付时间',
  INDEX `idx_expiry_time`(`expiry_time`, `success`) USING BTREE COMMENT '订阅过期时间',
  INDEX `idx_sandbox`(`sandbox`, `success`, `create_time`) USING BTREE COMMENT '沙盒环境',
  INDEX `idx_success`(`success`, `error_code`, `create_time`) USING BTREE COMMENT '发货成功',
  INDEX `idx_create_time`(`create_time`) USING BTREE COMMENT '创建时间',
  INDEX `idx_update_time`(`update_time`) USING BTREE COMMENT '更新时间'
) ENGINE = InnoDB AUTO_INCREMENT = 136 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '购买订单日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for register_group
-- ----------------------------
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

-- ----------------------------
-- Table structure for register_group_locale
-- ----------------------------
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

-- ----------------------------
-- Table structure for register_locale
-- ----------------------------
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

-- ----------------------------
-- Table structure for register_policy
-- ----------------------------
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

-- ----------------------------
-- Table structure for register_policy_group
-- ----------------------------
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

-- ----------------------------
-- Table structure for register_server_policy
-- ----------------------------
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

-- ----------------------------
-- Table structure for server
-- ----------------------------
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

-- ----------------------------
-- Table structure for strategy_device_name
-- ----------------------------
DROP TABLE IF EXISTS `strategy_device_name`;
CREATE TABLE `strategy_device_name`  (
  `device_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '设备名称',
  `valid` int(0) NOT NULL DEFAULT 0 COMMENT '是否有效, 0无效, 1有效',
  `resource_url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '资源下载地址',
  `gateway_host` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '网关地址',
  `gateway_port` int(0) NULL DEFAULT 0 COMMENT '网关端口',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`device_name`) USING BTREE,
  INDEX `idx_valid`(`valid`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '设备名称策略表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for strategy_ip
-- ----------------------------
DROP TABLE IF EXISTS `strategy_ip`;
CREATE TABLE `strategy_ip`  (
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'IP地址',
  `valid` int(0) NOT NULL DEFAULT 0 COMMENT '是否有效, 0无效, 1有效',
  `resource_url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '资源下载地址',
  `gateway_host` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '网关地址',
  `gateway_port` int(0) NULL DEFAULT 0 COMMENT '网关端口',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`ip`) USING BTREE,
  INDEX `idx_valid`(`valid`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'IP策略表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for strategy_version
-- ----------------------------
DROP TABLE IF EXISTS `strategy_version`;
CREATE TABLE `strategy_version`  (
  `version` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '版本',
  `examine` int(0) NOT NULL DEFAULT 0 COMMENT '审核状态,0=全部未通过,1=安卓通过,2=苹果通过,3=全部通过',
  `preview` int(0) NOT NULL DEFAULT 1 COMMENT '预览状态,0=false,1=true',
  `upgrade` int(0) NOT NULL DEFAULT -1 COMMENT '更新到本版本的方式,-1:默认规则,0=无更新,1=补丁更新,2=推荐更新,3=强制更新',
  `examine_resource_url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '审核资源下载地址',
  `examine_gateway_host` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '审核网关地址',
  `examine_gateway_port` int(0) NULL DEFAULT 0 COMMENT '审核网关端口',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`version`) USING BTREE,
  INDEX `idx_examine`(`examine`) USING BTREE,
  INDEX `idx_preview`(`preview`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '版本策略表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for super_login_account
-- ----------------------------
DROP TABLE IF EXISTS `super_login_account`;
CREATE TABLE `super_login_account`  (
  `account` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '三七账号',
  `valid` int(0) NOT NULL DEFAULT 0 COMMENT '是否有效, 0无效, 1有效',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`account`) USING BTREE,
  INDEX `idx_valid`(`valid`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '超级登录账号列表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ticket
-- ----------------------------
DROP TABLE IF EXISTS `ticket`;
CREATE TABLE `ticket`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `stub` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_stub`(`stub`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10320 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '唯一ID生成表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_account
-- ----------------------------
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

-- ----------------------------
-- Table structure for user_account_login_log
-- ----------------------------
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

-- ----------------------------
-- Table structure for user_account_reg
-- ----------------------------
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

-- ----------------------------
-- Table structure for user_migrate_log
-- ----------------------------
DROP TABLE IF EXISTS `user_migrate_log`;
CREATE TABLE `user_migrate_log`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `account_uid` bigint(0) NOT NULL COMMENT '账号ID',
  `game_uid` bigint(0) NOT NULL COMMENT '角色ID',
  `origin_server_id` int(0) NOT NULL COMMENT '源服务器ID',
  `target_server_id` int(0) NOT NULL COMMENT '目的服务器ID',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_account_uid`(`account_uid`) USING BTREE,
  INDEX `idx_game_uid`(`game_uid`) USING BTREE,
  INDEX `idx_origin_server_id`(`origin_server_id`) USING BTREE,
  INDEX `idx_target_server_id`(`target_server_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '迁服记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
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

-- ----------------------------
-- Table structure for user_role_reg
-- ----------------------------
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

-- ----------------------------
-- Table structure for user_subscription
-- ----------------------------
DROP TABLE IF EXISTS `user_subscription`;
CREATE TABLE `user_subscription`  (
  `account_uid` bigint(0) NOT NULL COMMENT '订阅账号ID',
  `game_uid` bigint(0) NOT NULL COMMENT '订阅角色ID',
  `server_id` int(0) NOT NULL COMMENT '订阅服务器ID',
  `start_time` bigint(0) NOT NULL COMMENT '订阅开始时间',
  `end_time` bigint(0) NOT NULL COMMENT '订阅结束时间',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`account_uid`) USING BTREE,
  INDEX `idx_game_uid`(`game_uid`) USING BTREE,
  INDEX `idx_server_id`(`server_id`) USING BTREE,
  INDEX `idx_start_time`(`start_time`) USING BTREE,
  INDEX `idx_end_time`(`end_time`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订阅表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
