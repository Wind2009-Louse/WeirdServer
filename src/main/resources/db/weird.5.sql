-- MySQL dump 10.13  Distrib 8.0.12, for Win64 (x86_64)
--
-- Host: localhost    Database: weird
-- ------------------------------------------------------
-- Server version	5.6.26-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
 SET NAMES utf8mb4 ;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `card_history`
--

DROP TABLE IF EXISTS `card_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `card_history` (
  `package_id` int(11) NOT NULL COMMENT '卡包ID',
  `card_pk` int(11) NOT NULL COMMENT '卡片PK',
  `old_name` varchar(45) NOT NULL DEFAULT '' COMMENT '原卡名',
  `new_name` varchar(45) NOT NULL DEFAULT '' COMMENT '现卡名',
  `rare` varchar(45) NOT NULL,
  `db_created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '数据库创建时间',
  `db_updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '数据库更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='卡片更改历史';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `card_history`
--

LOCK TABLES `card_history` WRITE;
/*!40000 ALTER TABLE `card_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `card_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `package_card`
--

DROP TABLE IF EXISTS `package_card`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `package_card` (
  `card_pk` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '卡片主键',
  `card_name` varchar(200) NOT NULL DEFAULT '' COMMENT '卡名',
  `package_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '所在卡包',
  `rare` varchar(45) NOT NULL DEFAULT 'N' COMMENT '稀有度',
  `db_created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '数据库创建时间',
  `db_updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '数据库更新时间',
  PRIMARY KEY (`card_pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='卡包中的卡片信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `package_card`
--

LOCK TABLES `package_card` WRITE;
/*!40000 ALTER TABLE `package_card` DISABLE KEYS */;
/*!40000 ALTER TABLE `package_card` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `package_info`
--

DROP TABLE IF EXISTS `package_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `package_info` (
  `package_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '卡包ID',
  `package_name` varchar(45) NOT NULL DEFAULT '' COMMENT '卡包名称',
  `db_created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '数据库创建时间',
  `db_updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '数据库更新时间',
  PRIMARY KEY (`package_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='卡包信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `package_info`
--

LOCK TABLES `package_info` WRITE;
/*!40000 ALTER TABLE `package_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `package_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roll_detail`
--

DROP TABLE IF EXISTS `roll_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `roll_detail` (
  `roll_id` bigint(20) NOT NULL COMMENT '抽卡记录ID',
  `card_pk` int(11) NOT NULL COMMENT '抽卡PK',
  `is_dust` tinyint(4) NOT NULL COMMENT '是否转化为尘',
  `db_created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '数据库创建时间',
  `db_updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '数据库更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='抽卡详细记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roll_detail`
--

LOCK TABLES `roll_detail` WRITE;
/*!40000 ALTER TABLE `roll_detail` DISABLE KEYS */;
/*!40000 ALTER TABLE `roll_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roll_list`
--

DROP TABLE IF EXISTS `roll_list`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `roll_list` (
  `roll_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '抽卡记录ID',
  `roll_user_id` int(11) NOT NULL DEFAULT '0' COMMENT '抽卡用户ID',
  `roll_package_id` int(11) NOT NULL DEFAULT '0' COMMENT '卡包ID',
  `is_disabled` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否被禁用（滚回）',
  `db_created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '数据库创建时间',
  `db_updated_tim` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '数据库更新e时间',
  PRIMARY KEY (`roll_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='抽卡列表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roll_list`
--

LOCK TABLES `roll_list` WRITE;
/*!40000 ALTER TABLE `roll_list` DISABLE KEYS */;
/*!40000 ALTER TABLE `roll_list` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_card_list`
--

DROP TABLE IF EXISTS `user_card_list`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `user_card_list` (
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `card_pk` int(11) NOT NULL DEFAULT '0' COMMENT '卡片PK',
  `count` int(11) NOT NULL DEFAULT '0' COMMENT '持有数量',
  `db_created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '数据库创建时间',
  `db_updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '数据库更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户持有的卡片';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_card_list`
--

LOCK TABLES `user_card_list` WRITE;
/*!40000 ALTER TABLE `user_card_list` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_card_list` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_data`
--

DROP TABLE IF EXISTS `user_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `user_data` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `user_name` varchar(45) NOT NULL DEFAULT '' COMMENT '用户名',
  `password` varchar(100) NOT NULL DEFAULT '' COMMENT '用户密码',
  `is_admin` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否管理员',
  `nonaward_count` int(11) NOT NULL DEFAULT '0' COMMENT '未出货的计数',
  `dust_count` int(11) NOT NULL DEFAULT '0' COMMENT '尘数',
  `duel_point` int NOT NULL DEFAULT '0' COMMENT 'DP',
  `daily_win` int NOT NULL DEFAULT '0' COMMENT '当天胜利次数',
  `daily_lost` int NOT NULL DEFAULT '0' COMMENT '当天失败次数',
  `daily_award` int NOT NULL DEFAULT '0' COMMENT '当天是否出货',
  `weekly_dust_change_n` int NOT NULL DEFAULT '0' COMMENT '每周换NR的次数',
  `weekly_dust_change_r` int NOT NULL DEFAULT '0' COMMENT '每周换随机闪的次数',
  `weekly_dust_change_alter` int NOT NULL DEFAULT '0' COMMENT '每周换自选闪的次数',
  `db_created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '数据库创建时间',
  `db_updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '数据库更新时间',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_data`
--

LOCK TABLES `user_data` WRITE;
/*!40000 ALTER TABLE `user_data` DISABLE KEYS */;
INSERT INTO `user_data` (`user_name`,`password`,`is_admin`,`nonaward_count`,`dust_count`,`duel_point`) VALUES ("admin","E10ADC3949BA59ABBE56E057F20F883E",1,0,0,0);
/*!40000 ALTER TABLE `user_data` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-09-09 16:57:
