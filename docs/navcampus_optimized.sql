-- ============================================================
-- NavCampus 智慧校园综合导航平台 - 数据库初始化脚本
-- 优化目标: MySQL 8.4 LTS (utf8mb4_0900_ai_ci)
-- 生成日期: 2026-08-08
-- 说明: 表结构已针对 MySQL 8.x 优化，数据保持原样不变
-- ============================================================

-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- 主机： localhost
-- 生成日期： 2026-08-08 12:22:53
-- 服务器版本： 5.7.44-log
-- PHP 版本： 8.0.26

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
-- 创建数据库
CREATE DATABASE IF NOT EXISTS `navcampus` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `navcampus`;



--
-- 数据库： `navcampus`
--

-- --------------------------------------------------------

--
-- 表的结构 `campus_landmark`
--

DROP TABLE IF EXISTS `campus_landmark`;
CREATE TABLE `campus_landmark` (
  `landmark_id` bigint NOT NULL COMMENT '地标唯一ID',
  `landmark_name` varchar(100) NOT NULL COMMENT '地标名称（如：一号教学楼、食堂）',
  `landmark_type` tinyint NOT NULL COMMENT '地标类型：1-教学楼 2-宿舍 3-食堂 4-图书馆 5-校门 6-其他',
  `address` varchar(200) DEFAULT NULL COMMENT '详细地址',
  `description` text COMMENT '地标描述、开放时间、楼层分布',
  `node_id` bigint NOT NULL COMMENT '关联路网节点ID（对应campus_road_node表）',
  `x_coordinate` decimal(10,6) NOT NULL COMMENT '3D模型X坐标',
  `y_coordinate` decimal(10,6) NOT NULL COMMENT '3D模型Y坐标',
  `z_coordinate` decimal(10,6) NOT NULL COMMENT '3D模型Z坐标',
  `sort` int DEFAULT '0' COMMENT '排序权重',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用 1-正常',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `del_flag` tinyint DEFAULT '0' COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='校园地标表';

--
-- 转存表中的数据 `campus_landmark`
--

INSERT INTO `campus_landmark` (`landmark_id`, `landmark_name`, `landmark_type`, `address`, `description`, `node_id`, `x_coordinate`, `y_coordinate`, `z_coordinate`, `sort`, `status`, `create_time`, `update_time`, `del_flag`) VALUES
(9, 'A座教学楼', 1, NULL, NULL, 1, '283.500000', '7.000000', '55.300000', 0, 1, '2026-03-16 12:06:31', '2026-03-16 12:06:31', 0),
(10, 'B座教学楼', 1, NULL, NULL, 2, '237.400000', '7.000000', '76.600000', 0, 1, '2026-03-16 12:06:31', '2026-03-16 12:06:31', 0),
(11, 'C座教学楼', 1, NULL, NULL, 3, '191.300000', '7.000000', '96.300000', 0, 1, '2026-03-16 12:06:31', '2026-03-16 12:06:31', 0),
(12, 'D座教学楼', 1, NULL, NULL, 4, '148.600000', '7.000000', '114.000000', 0, 1, '2026-03-16 12:06:31', '2026-03-16 12:06:31', 0),
(13, 'E座教学楼', 1, NULL, NULL, 5, '114.000000', '8.000000', '147.000000', 0, 1, '2026-03-16 12:06:31', '2026-03-16 12:06:31', 0),
(14, 'F座教学楼', 1, NULL, NULL, 6, '142.400000', '7.000000', '51.400000', 0, 1, '2026-03-16 12:06:31', '2026-03-16 12:06:31', 0),
(15, 'G座教学楼', 1, NULL, NULL, 7, '191.400000', '7.000000', '28.900000', 0, 1, '2026-03-16 12:06:31', '2026-03-16 12:06:31', 0),
(16, '文科楼', 1, NULL, NULL, 8, '-82.800000', '6.000000', '189.500000', 0, 1, '2026-03-16 12:06:31', '2026-03-16 12:06:31', 0),
(17, '信息科学与工程学院', 1, NULL, NULL, 9, '474.000000', '10.000000', '23.500000', 0, 1, '2026-03-16 12:06:31', '2026-03-16 12:06:31', 0),
(18, '电气工程学院', 1, NULL, NULL, 10, '564.000000', '7.000000', '-50.500000', 0, 1, '2026-03-16 12:06:31', '2026-03-16 12:06:31', 0),
(19, '理学院', 1, NULL, NULL, 11, '477.000000', '6.000000', '-150.000000', 0, 1, '2026-03-16 12:06:31', '2026-03-16 12:06:31', 0),
(20, '机械学院', 1, NULL, NULL, 12, '520.000000', '6.000000', '-170.000000', 0, 1, '2026-03-16 12:06:31', '2026-03-16 12:06:31', 0),
(21, '图书馆', 4, NULL, NULL, 13, '258.000000', '11.400000', '-122.800000', 0, 1, '2026-03-16 12:06:31', '2026-03-16 12:06:31', 0),
(22, '教师公寓', 2, NULL, NULL, 17, '-158.000000', '7.000000', '361.000000', 0, 1, '2026-03-16 12:06:31', '2026-03-16 12:06:31', 0),
(23, 'C1宿舍楼', 2, NULL, NULL, 27, '-266.000000', '7.000000', '112.000000', 0, 1, '2026-03-16 12:06:31', '2026-03-16 12:06:31', 0),
(24, 'C2宿舍楼', 2, NULL, NULL, 28, '-364.200000', '7.000000', '113.700000', 0, 1, '2026-03-16 12:06:31', '2026-03-16 12:06:31', 0),
(25, 'C3宿舍楼', 2, NULL, NULL, 29, '-264.800000', '7.000000', '159.500000', 0, 1, '2026-03-16 12:06:31', '2026-03-16 12:06:31', 0),
(26, 'C4宿舍楼', 2, NULL, NULL, 30, '-363.300000', '7.000000', '160.700000', 0, 1, '2026-03-16 12:06:31', '2026-03-16 12:06:31', 0),
(27, 'C5宿舍楼', 2, NULL, NULL, 31, '-361.800000', '7.000000', '255.400000', 0, 1, '2026-03-16 12:06:31', '2026-03-16 12:06:31', 0),
(28, 'C6宿舍楼', 2, NULL, NULL, 32, '-355.500000', '7.000000', '335.500000', 0, 1, '2026-03-16 12:06:31', '2026-03-16 12:06:31', 0),
(29, 'C7宿舍楼', 2, NULL, NULL, 33, '-358.600000', '7.000000', '447.800000', 0, 1, '2026-03-16 12:06:31', '2026-03-16 12:06:31', 0),
(30, 'B1宿舍楼', 2, NULL, NULL, 34, '-63.300000', '7.000000', '-391.700000', 0, 1, '2026-03-16 12:06:31', '2026-03-16 12:06:31', 0),
(31, 'B2宿舍楼', 2, NULL, NULL, 35, '-63.200000', '7.000000', '-335.300000', 0, 1, '2026-03-16 12:06:31', '2026-03-16 12:06:31', 0),
(32, 'A1宿舍楼', 2, NULL, NULL, 36, '307.000000', '7.000000', '-463.500000', 0, 1, '2026-03-16 12:06:31', '2026-03-16 12:06:31', 0),
(33, 'A2宿舍楼', 2, NULL, NULL, 37, '207.000000', '7.000000', '-477.800000', 0, 1, '2026-03-16 12:06:31', '2026-03-16 12:06:31', 0),
(34, 'A3宿舍楼', 2, NULL, NULL, 38, '109.800000', '7.000000', '-479.000000', 0, 1, '2026-03-16 12:06:31', '2026-03-16 12:06:31', 0),
(35, 'A4宿舍楼', 2, NULL, NULL, 39, '238.000000', '7.000000', '-380.200000', 0, 1, '2026-03-16 12:06:31', '2026-03-16 12:06:31', 0),
(36, 'A5宿舍楼', 2, NULL, NULL, 40, '188.400000', '7.000000', '-356.000000', 0, 1, '2026-03-16 12:06:31', '2026-03-16 12:06:31', 0),
(37, 'A6宿舍楼', 2, NULL, NULL, 41, '138.500000', '7.000000', '-331.600000', 0, 1, '2026-03-16 12:06:31', '2026-03-16 12:06:31', 0),
(38, '南食堂', 3, NULL, NULL, 23, '-109.000000', '7.000000', '-221.500000', 0, 1, '2026-03-16 12:06:31', '2026-03-16 12:06:31', 0),
(39, '北食堂', 3, NULL, NULL, 25, '294.500000', '7.000000', '-359.500000', 0, 1, '2026-03-16 12:06:31', '2026-03-16 12:06:31', 0),
(40, '东门', 5, NULL, NULL, 19, '399.300000', '7.000000', '203.000000', 0, 1, '2026-03-16 12:06:31', '2026-03-16 12:06:31', 0),
(41, '南门', 5, NULL, NULL, 20, '-414.500000', '7.000000', '-102.000000', 0, 1, '2026-03-16 12:06:31', '2026-03-16 12:06:31', 0),
(42, '南角门', 5, NULL, NULL, 21, '-407.500000', '7.000000', '322.500000', 0, 1, '2026-03-16 12:06:31', '2026-03-16 12:06:31', 0),
(43, '北门', 5, NULL, NULL, 22, '701.500000', '6.000000', '-345.000000', 0, 1, '2026-03-16 12:06:31', '2026-03-16 12:06:31', 0),
(44, '校部', 6, NULL, NULL, 14, '466.000000', '11.500000', '153.500000', 0, 1, '2026-03-16 12:06:31', '2026-03-16 12:06:31', 0),
(45, '大学生活动中心', 6, NULL, NULL, 15, '-193.000000', '7.000000', '-62.000000', 0, 1, '2026-03-16 12:06:31', '2026-03-16 12:06:31', 0),
(46, '校医院', 6, NULL, NULL, 16, '-198.500000', '7.000000', '67.000000', 0, 1, '2026-03-16 12:06:31', '2026-03-16 12:06:31', 0),
(47, '超市', 6, NULL, NULL, 18, '-195.500000', '7.000000', '-141.000000', 0, 1, '2026-03-16 12:06:31', '2026-03-16 12:06:31', 0),
(48, '南体育场', 6, NULL, NULL, 24, '-302.800000', '7.000000', '-305.000000', 0, 1, '2026-03-16 12:06:31', '2026-03-16 12:06:31', 0),
(49, '北体育场', 6, NULL, NULL, 26, '460.000000', '7.000000', '-446.500000', 0, 1, '2026-03-16 12:06:31', '2026-03-16 12:06:31', 0);

-- --------------------------------------------------------

--
-- 表的结构 `campus_road_edge`
--

DROP TABLE IF EXISTS `campus_road_edge`;
CREATE TABLE `campus_road_edge` (
  `edge_id` bigint NOT NULL COMMENT '边唯一ID',
  `start_node_id` bigint NOT NULL COMMENT '起点节点ID（关联campus_road_node）',
  `end_node_id` bigint NOT NULL COMMENT '终点节点ID（关联campus_road_node）',
  `distance` decimal(8,2) NOT NULL COMMENT '边的长度（米），路径权重核心值',
  `is_one_way` tinyint DEFAULT '0' COMMENT '是否单向通行：0-双向 1-单向',
  `difficulty` tinyint DEFAULT '1' COMMENT '通行难度：1-平坦 2-有台阶 3-坡度大，用于路径优化',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用 1-正常',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `del_flag` tinyint DEFAULT '0' COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='校园路网边表';

--
-- 转存表中的数据 `campus_road_edge`
--

INSERT INTO `campus_road_edge` (`edge_id`, `start_node_id`, `end_node_id`, `distance`, `is_one_way`, `difficulty`, `status`, `create_time`, `update_time`, `del_flag`) VALUES
(27, 4, 9, '10.00', 0, 1, 1, '2026-03-07 16:33:30', '2026-03-07 16:33:30', 0),
(28, 9, 14, '10.00', 0, 1, 1, '2026-03-07 16:33:30', '2026-03-07 16:33:30', 0),
(29, 14, 19, '10.00', 0, 1, 1, '2026-03-07 16:33:30', '2026-03-07 16:33:30', 0),
(30, 5, 10, '10.00', 0, 1, 1, '2026-03-07 16:33:30', '2026-03-07 16:33:30', 0),
(31, 10, 15, '10.00', 0, 1, 1, '2026-03-07 16:33:30', '2026-03-07 16:33:30', 0),
(32, 15, 20, '10.00', 0, 1, 1, '2026-03-07 16:33:30', '2026-03-07 16:33:30', 0),
(33, 7, 13, '14.14', 0, 1, 1, '2026-03-07 16:33:30', '2026-03-07 16:33:30', 0),
(34, 13, 19, '14.14', 0, 1, 1, '2026-03-07 16:33:30', '2026-03-07 16:33:30', 0);

-- --------------------------------------------------------

--
-- 表的结构 `campus_road_node`
--

DROP TABLE IF EXISTS `campus_road_node`;
CREATE TABLE `campus_road_node` (
  `node_id` bigint NOT NULL COMMENT '节点唯一ID',
  `node_name` varchar(100) DEFAULT NULL COMMENT '节点名称（可选）',
  `x_coordinate` decimal(10,6) NOT NULL COMMENT '3D模型X坐标',
  `y_coordinate` decimal(10,6) NOT NULL COMMENT '3D模型Y坐标',
  `z_coordinate` decimal(10,6) NOT NULL COMMENT '3D模型Z坐标',
  `is_landmark` tinyint DEFAULT '0' COMMENT '是否为地标节点：0-否 1-是',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用（施工/不可通行） 1-正常',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `del_flag` tinyint DEFAULT '0' COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='校园路网节点表';

-- --------------------------------------------------------

--
-- 表的结构 `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `user_id` bigint NOT NULL COMMENT '用户唯一ID',
  `username` varchar(50) NOT NULL COMMENT '用户名（学号）',
  `password` varchar(100) NOT NULL COMMENT '加密存储的密码（BCrypt加密）',
  `real_name` varchar(20) DEFAULT NULL COMMENT '真实姓名',
  `college` varchar(50) DEFAULT NULL COMMENT '学院',
  `major` varchar(50) DEFAULT NULL COMMENT '专业',
  `status` tinyint DEFAULT '1' COMMENT '账号状态：0-禁用 1-正常',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `del_flag` tinyint DEFAULT '0' COMMENT '逻辑删除：0-未删除 1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户信息表';

--
-- 转存表中的数据 `sys_user`
--

INSERT INTO `sys_user` (`user_id`, `username`, `password`, `real_name`, `college`, `major`, `status`, `create_time`, `update_time`, `del_flag`) VALUES
(1, 'hongyun', '$2a$10$c6R//DaPmSK.FFScKlwIkeuCjAbnIeL17jmY74ICxn0tY4hNIwzv.', '洪运', '软件学院', '软件工程', 1, '2026-03-06 08:55:42', '2026-03-06 08:55:42', 0);

-- --------------------------------------------------------

--
-- 表的结构 `user_course`
--

DROP TABLE IF EXISTS `user_course`;
CREATE TABLE `user_course` (
  `course_id` bigint NOT NULL COMMENT '课程唯一ID',
  `user_id` bigint NOT NULL COMMENT '所属用户ID（关联sys_user）',
  `course_name` varchar(100) NOT NULL COMMENT '课程名称',
  `teacher_name` varchar(20) DEFAULT NULL COMMENT '授课教师',
  `week_day` tinyint NOT NULL COMMENT '星期：1-周一 2-周二 ... 7-周日',
  `start_section` tinyint NOT NULL COMMENT '开始节次',
  `end_section` tinyint NOT NULL COMMENT '结束节次',
  `classroom_name` varchar(50) NOT NULL COMMENT '教室名称',
  `landmark_id` bigint NOT NULL COMMENT '对应教室地标ID（关联campus_landmark）',
  `start_week` tinyint DEFAULT '1' COMMENT '开始周次',
  `end_week` tinyint DEFAULT '18' COMMENT '结束周次',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `del_flag` tinyint DEFAULT '0' COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户课程表';

--
-- 转储表的索引
--

--
-- 表的索引 `campus_landmark`
--
ALTER TABLE `campus_landmark`
  ADD PRIMARY KEY (`landmark_id`),
  ADD UNIQUE KEY `uk_node_id` (`node_id`),
  ADD KEY `idx_landmark_type` (`landmark_type`),
  ADD KEY `idx_landmark_name` (`landmark_name`);

--
-- 表的索引 `campus_road_edge`
--
ALTER TABLE `campus_road_edge`
  ADD PRIMARY KEY (`edge_id`),
  ADD UNIQUE KEY `uk_start_end_node` (`start_node_id`,`end_node_id`),
  ADD KEY `idx_start_node` (`start_node_id`),
  ADD KEY `idx_end_node` (`end_node_id`);

--
-- 表的索引 `campus_road_node`
--
ALTER TABLE `campus_road_node`
  ADD PRIMARY KEY (`node_id`),
  ADD KEY `idx_is_landmark` (`is_landmark`);

--
-- 表的索引 `sys_user`
--
ALTER TABLE `sys_user`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `uk_username` (`username`);

--
-- 表的索引 `user_course`
--
ALTER TABLE `user_course`
  ADD PRIMARY KEY (`course_id`),
  ADD KEY `idx_user_id` (`user_id`),
  ADD KEY `idx_landmark_id` (`landmark_id`);

--
-- 在导出的表使用AUTO_INCREMENT
--

--
-- 使用表AUTO_INCREMENT `campus_landmark`
--
ALTER TABLE `campus_landmark`
  MODIFY `landmark_id` bigint NOT NULL AUTO_INCREMENT COMMENT '地标唯一ID', AUTO_INCREMENT=50;

--
-- 使用表AUTO_INCREMENT `campus_road_edge`
--
ALTER TABLE `campus_road_edge`
  MODIFY `edge_id` bigint NOT NULL AUTO_INCREMENT COMMENT '边唯一ID', AUTO_INCREMENT=35;

--
-- 使用表AUTO_INCREMENT `campus_road_node`
--
ALTER TABLE `campus_road_node`
  MODIFY `node_id` bigint NOT NULL AUTO_INCREMENT COMMENT '节点唯一ID', AUTO_INCREMENT=21;

--
-- 使用表AUTO_INCREMENT `sys_user`
--
ALTER TABLE `sys_user`
  MODIFY `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户唯一ID', AUTO_INCREMENT=2;

--
-- 使用表AUTO_INCREMENT `user_course`
--
ALTER TABLE `user_course`
  MODIFY `course_id` bigint NOT NULL AUTO_INCREMENT COMMENT '课程唯一ID';
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
