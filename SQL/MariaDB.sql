-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               11.6.2-MariaDB - mariadb.org binary distribution
-- Server OS:                    Win64
-- HeidiSQL Version:             12.8.0.6908
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for savorgo
DROP DATABASE IF EXISTS `savorgo`;
CREATE DATABASE IF NOT EXISTS `savorgo` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_uca1400_ai_ci */;
USE `savorgo`;

-- Dumping structure for table savorgo.auth_group
DROP TABLE IF EXISTS `auth_group`;
CREATE TABLE IF NOT EXISTS `auth_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(150) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Dumping data for table savorgo.auth_group: ~0 rows (approximately)
DELETE FROM `auth_group`;

-- Dumping structure for table savorgo.auth_group_permissions
DROP TABLE IF EXISTS `auth_group_permissions`;
CREATE TABLE IF NOT EXISTS `auth_group_permissions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `group_id` int(11) NOT NULL,
  `permission_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `auth_group_permissions_group_id_permission_id_0cd325b0_uniq` (`group_id`,`permission_id`),
  KEY `auth_group_permissio_permission_id_84c5c92e_fk_auth_perm` (`permission_id`),
  CONSTRAINT `auth_group_permissio_permission_id_84c5c92e_fk_auth_perm` FOREIGN KEY (`permission_id`) REFERENCES `auth_permission` (`id`),
  CONSTRAINT `auth_group_permissions_group_id_b120cbf9_fk_auth_group_id` FOREIGN KEY (`group_id`) REFERENCES `auth_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Dumping data for table savorgo.auth_group_permissions: ~0 rows (approximately)
DELETE FROM `auth_group_permissions`;

-- Dumping structure for table savorgo.auth_permission
DROP TABLE IF EXISTS `auth_permission`;
CREATE TABLE IF NOT EXISTS `auth_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `content_type_id` int(11) NOT NULL,
  `codename` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `auth_permission_content_type_id_codename_01ab375a_uniq` (`content_type_id`,`codename`),
  CONSTRAINT `auth_permission_content_type_id_2f476e4b_fk_django_co` FOREIGN KEY (`content_type_id`) REFERENCES `django_content_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Dumping data for table savorgo.auth_permission: ~0 rows (approximately)
DELETE FROM `auth_permission`;

-- Dumping structure for table savorgo.auth_user
DROP TABLE IF EXISTS `auth_user`;
CREATE TABLE IF NOT EXISTS `auth_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `password` varchar(128) NOT NULL,
  `last_login` datetime(6) DEFAULT NULL,
  `is_superuser` tinyint(1) NOT NULL,
  `username` varchar(150) NOT NULL,
  `first_name` varchar(150) NOT NULL,
  `last_name` varchar(150) NOT NULL,
  `email` varchar(254) NOT NULL,
  `is_staff` tinyint(1) NOT NULL,
  `is_active` tinyint(1) NOT NULL,
  `date_joined` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Dumping data for table savorgo.auth_user: ~0 rows (approximately)
DELETE FROM `auth_user`;

-- Dumping structure for table savorgo.auth_user_groups
DROP TABLE IF EXISTS `auth_user_groups`;
CREATE TABLE IF NOT EXISTS `auth_user_groups` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `group_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `auth_user_groups_user_id_group_id_94350c0c_uniq` (`user_id`,`group_id`),
  KEY `auth_user_groups_group_id_97559544_fk_auth_group_id` (`group_id`),
  CONSTRAINT `auth_user_groups_group_id_97559544_fk_auth_group_id` FOREIGN KEY (`group_id`) REFERENCES `auth_group` (`id`),
  CONSTRAINT `auth_user_groups_user_id_6a12ed8b_fk_auth_user_id` FOREIGN KEY (`user_id`) REFERENCES `auth_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Dumping data for table savorgo.auth_user_groups: ~0 rows (approximately)
DELETE FROM `auth_user_groups`;

-- Dumping structure for table savorgo.auth_user_user_permissions
DROP TABLE IF EXISTS `auth_user_user_permissions`;
CREATE TABLE IF NOT EXISTS `auth_user_user_permissions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `permission_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `auth_user_user_permissions_user_id_permission_id_14a6b632_uniq` (`user_id`,`permission_id`),
  KEY `auth_user_user_permi_permission_id_1fbb5f2c_fk_auth_perm` (`permission_id`),
  CONSTRAINT `auth_user_user_permi_permission_id_1fbb5f2c_fk_auth_perm` FOREIGN KEY (`permission_id`) REFERENCES `auth_permission` (`id`),
  CONSTRAINT `auth_user_user_permissions_user_id_a95ead1b_fk_auth_user_id` FOREIGN KEY (`user_id`) REFERENCES `auth_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Dumping data for table savorgo.auth_user_user_permissions: ~0 rows (approximately)
DELETE FROM `auth_user_user_permissions`;

-- Dumping structure for table savorgo.django_admin_log
DROP TABLE IF EXISTS `django_admin_log`;
CREATE TABLE IF NOT EXISTS `django_admin_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `action_time` datetime(6) NOT NULL,
  `object_id` longtext DEFAULT NULL,
  `object_repr` varchar(200) NOT NULL,
  `action_flag` smallint(5) unsigned NOT NULL CHECK (`action_flag` >= 0),
  `change_message` longtext NOT NULL,
  `content_type_id` int(11) DEFAULT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `django_admin_log_content_type_id_c4bce8eb_fk_django_co` (`content_type_id`),
  KEY `django_admin_log_user_id_c564eba6_fk_auth_user_id` (`user_id`),
  CONSTRAINT `django_admin_log_content_type_id_c4bce8eb_fk_django_co` FOREIGN KEY (`content_type_id`) REFERENCES `django_content_type` (`id`),
  CONSTRAINT `django_admin_log_user_id_c564eba6_fk_auth_user_id` FOREIGN KEY (`user_id`) REFERENCES `auth_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Dumping data for table savorgo.django_admin_log: ~0 rows (approximately)
DELETE FROM `django_admin_log`;

-- Dumping structure for table savorgo.django_content_type
DROP TABLE IF EXISTS `django_content_type`;
CREATE TABLE IF NOT EXISTS `django_content_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `app_label` varchar(100) NOT NULL,
  `model` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `django_content_type_app_label_model_76bd3d3b_uniq` (`app_label`,`model`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Dumping data for table savorgo.django_content_type: ~0 rows (approximately)
DELETE FROM `django_content_type`;

-- Dumping structure for table savorgo.django_migrations
DROP TABLE IF EXISTS `django_migrations`;
CREATE TABLE IF NOT EXISTS `django_migrations` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `app` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `applied` datetime(6) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Dumping data for table savorgo.django_migrations: ~19 rows (approximately)
DELETE FROM `django_migrations`;
INSERT INTO `django_migrations` (`id`, `app`, `name`, `applied`) VALUES
	(1, 'contenttypes', '0001_initial', '2025-01-15 08:06:50.452793'),
	(2, 'auth', '0001_initial', '2025-01-15 08:06:50.637544'),
	(3, 'admin', '0001_initial', '2025-01-15 08:06:50.678193'),
	(4, 'admin', '0002_logentry_remove_auto_add', '2025-01-15 08:06:50.684130'),
	(5, 'admin', '0003_logentry_add_action_flag_choices', '2025-01-15 08:06:50.690098'),
	(6, 'contenttypes', '0002_remove_content_type_name', '2025-01-15 08:06:50.724163'),
	(7, 'auth', '0002_alter_permission_name_max_length', '2025-01-15 08:06:50.745818'),
	(8, 'auth', '0003_alter_user_email_max_length', '2025-01-15 08:06:50.761248'),
	(9, 'auth', '0004_alter_user_username_opts', '2025-01-15 08:06:50.766765'),
	(10, 'auth', '0005_alter_user_last_login_null', '2025-01-15 08:06:50.787440'),
	(11, 'auth', '0006_require_contenttypes_0002', '2025-01-15 08:06:50.789041'),
	(12, 'auth', '0007_alter_validators_add_error_messages', '2025-01-15 08:06:50.794957'),
	(13, 'auth', '0008_alter_user_username_max_length', '2025-01-15 08:06:50.809459'),
	(14, 'auth', '0009_alter_user_last_name_max_length', '2025-01-15 08:06:50.824521'),
	(15, 'auth', '0010_alter_group_name_max_length', '2025-01-15 08:06:50.839305'),
	(16, 'auth', '0011_update_proxy_permissions', '2025-01-15 08:06:50.845096'),
	(17, 'auth', '0012_alter_user_first_name_max_length', '2025-01-15 08:06:50.864113'),
	(18, 'sessions', '0001_initial', '2025-01-15 08:06:50.881852'),
	(19, 'menu_management', '0001_initial', '2025-01-17 09:12:21.263081');

-- Dumping structure for table savorgo.django_session
DROP TABLE IF EXISTS `django_session`;
CREATE TABLE IF NOT EXISTS `django_session` (
  `session_key` varchar(40) NOT NULL,
  `session_data` longtext NOT NULL,
  `expire_date` datetime(6) NOT NULL,
  PRIMARY KEY (`session_key`),
  KEY `django_session_expire_date_a5c62663` (`expire_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Dumping data for table savorgo.django_session: ~0 rows (approximately)
DELETE FROM `django_session`;

-- Dumping structure for procedure savorgo.InsertTables
DROP PROCEDURE IF EXISTS `InsertTables`;
DELIMITER //
CREATE PROCEDURE `InsertTables`()
BEGIN
    DECLARE i INT DEFAULT 1;
    WHILE i <= 10 DO
        INSERT INTO `tables` (`name`, `status`, `reserved_time`, `created_time`, `modified_time`)
        VALUES (
            CONCAT('Table_', i), 
            CASE 
                WHEN i % 5 = 1 THEN 'AVAILABLE'
                WHEN i % 5 = 2 THEN 'OUT_OF_SERVICE'
                WHEN i % 5 = 3 THEN 'OCCUPIED'
                WHEN i % 5 = 4 THEN 'NEEDS_CLEANING'
                ELSE 'DELETED'
            END,
            NULL,
            CURRENT_TIMESTAMP(),
            CURRENT_TIMESTAMP()
        );
        SET i = i + 1;
    END WHILE;
END//
DELIMITER ;

-- Dumping structure for table savorgo.promotions
DROP TABLE IF EXISTS `promotions`;
CREATE TABLE IF NOT EXISTS `promotions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `discount_value` bigint(20) NOT NULL,
  `discount_type` enum('PERCENT','FLAT') NOT NULL,
  `menu_id` varchar(24) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `status` enum('AVAILABLE','ENDED','DELETED') NOT NULL,
  `created_time` timestamp NULL DEFAULT current_timestamp(),
  `modified_time` timestamp NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Dumping data for table savorgo.promotions: ~6 rows (approximately)
DELETE FROM `promotions`;
INSERT INTO `promotions` (`id`, `name`, `discount_value`, `discount_type`, `menu_id`, `start_date`, `end_date`, `status`, `created_time`, `modified_time`) VALUES
	(2, 'Siêu ưu đãi', 1, 'FLAT', '6793901c2251b721c39a9297', '2025-02-20', NULL, 'DELETED', '2025-01-30 22:02:26', '2025-02-01 23:41:45'),
	(3, 'GIảm giá bánh bông lan', 1, 'PERCENT', '6793901c2251b721c39a9293', NULL, NULL, 'AVAILABLE', '2025-02-01 23:40:49', '2025-02-01 23:40:49'),
	(4, 'Giảm giá banh flan thơm ngon mời bạn ăn nha', 20, 'PERCENT', '6793901c2251b721c39a9286', NULL, NULL, 'AVAILABLE', '2025-02-03 04:57:18', '2025-02-03 04:57:18'),
	(5, 'kjsaf', 100, 'FLAT', '6793901c2251b721c39a9281', NULL, NULL, 'AVAILABLE', '2025-02-03 04:57:18', '2025-02-03 04:57:18'),
	(6, 'Bánh xèo', 100, 'FLAT', '6793901c2251b721c39a9288', NULL, NULL, 'AVAILABLE', '2025-02-03 04:57:18', '2025-02-03 04:57:18'),
	(7, 'cas chien gion', 20, 'FLAT', '6793901c2251b721c39a9279', NULL, NULL, 'AVAILABLE', '2025-02-03 05:07:42', '2025-02-03 05:07:42');

-- Dumping structure for table savorgo.tables
DROP TABLE IF EXISTS `tables`;
CREATE TABLE IF NOT EXISTS `tables` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `status` enum('AVAILABLE','OUT_OF_SERVICE','OCCUPIED','NEEDS_CLEANING','DELETED') NOT NULL,
  `reserved_time` datetime DEFAULT NULL,
  `created_time` datetime DEFAULT current_timestamp(),
  `modified_time` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Dumping data for table savorgo.tables: ~20 rows (approximately)
DELETE FROM `tables`;
INSERT INTO `tables` (`id`, `name`, `status`, `reserved_time`, `created_time`, `modified_time`) VALUES
	(34, 'Cozy Table for 2', 'AVAILABLE', NULL, '2025-02-07 15:56:59', '2025-02-07 15:56:59'),
	(35, 'Family Table for 6', 'OCCUPIED', '2025-02-07 18:30:00', '2025-02-07 15:56:59', '2025-02-07 15:56:59'),
	(36, 'Romantic Table for 2', 'OUT_OF_SERVICE', NULL, '2025-02-07 15:56:59', '2025-02-07 15:56:59'),
	(37, 'VIP Table for 4', 'AVAILABLE', NULL, '2025-02-07 15:56:59', '2025-02-07 15:56:59'),
	(38, 'Bar Seat for 1', 'AVAILABLE', NULL, '2025-02-07 15:56:59', '2025-02-07 15:56:59'),
	(39, 'Outdoor Table for 4', 'NEEDS_CLEANING', NULL, '2025-02-07 15:56:59', '2025-02-07 15:56:59'),
	(40, 'Window Seat for 2', 'AVAILABLE', NULL, '2025-02-07 15:56:59', '2025-02-07 15:56:59'),
	(41, 'Chef’s Table for 8', 'OCCUPIED', '2025-02-07 19:00:00', '2025-02-07 15:56:59', '2025-02-07 15:56:59'),
	(42, 'Corner Booth for 6', 'OUT_OF_SERVICE', NULL, '2025-02-07 15:56:59', '2025-02-07 15:56:59'),
	(43, 'Private Table for 4', 'AVAILABLE', NULL, '2025-02-07 15:56:59', '2025-02-07 15:56:59'),
	(44, 'Balcony Table for 2', 'DELETED', NULL, '2025-02-07 15:56:59', '2025-02-07 15:56:59'),
	(45, 'Shared Table for 10', 'AVAILABLE', NULL, '2025-02-07 15:56:59', '2025-02-07 15:56:59'),
	(46, 'Garden Table for 4', 'OCCUPIED', '2025-02-07 20:15:00', '2025-02-07 15:56:59', '2025-02-07 15:56:59'),
	(47, 'Lounge Sofa for 3', 'AVAILABLE', NULL, '2025-02-07 15:56:59', '2025-02-07 15:56:59'),
	(48, 'Terrace Table for 5', 'NEEDS_CLEANING', NULL, '2025-02-07 15:56:59', '2025-02-07 15:56:59'),
	(49, 'Executive Table for 6', 'OUT_OF_SERVICE', NULL, '2025-02-07 15:56:59', '2025-02-07 15:56:59'),
	(50, 'Round Table for 4', 'OCCUPIED', '2025-02-07 21:00:00', '2025-02-07 15:56:59', '2025-02-07 15:56:59'),
	(51, 'Patio Table for 2', 'AVAILABLE', NULL, '2025-02-07 15:56:59', '2025-02-07 15:56:59'),
	(52, 'Booth Table for 6', 'NEEDS_CLEANING', NULL, '2025-02-07 15:56:59', '2025-02-07 15:56:59'),
	(53, 'Counter Seat for 1', 'AVAILABLE', NULL, '2025-02-07 15:56:59', '2025-02-07 15:56:59');

-- Dumping structure for table savorgo.users
DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `id` varchar(255) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `created_time` datetime DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `modified_time` datetime DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `points` int(11) NOT NULL,
  `role` enum('CUSTOMER','GUEST','MANAGER','STAFF') DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL CHECK (`status` between 0 and 1),
  `tier` enum('COPPER','DIAMOND','GOLD','NONE') DEFAULT NULL,
  `public_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table savorgo.users: ~4 rows (approximately)
DELETE FROM `users`;
INSERT INTO `users` (`id`, `address`, `created_time`, `email`, `first_name`, `last_name`, `modified_time`, `password`, `points`, `role`, `status`, `tier`, `public_id`) VALUES
	('0e8bb53a-6ddb-4674-8bca-c42a47f8de97', 'Bến Tre', '2025-02-06 13:35:15', 'thinhdev2003@gmail.com', 'Thịnh', 'Hồ Huỳnh Hoài', '2025-02-07 15:48:15', '$2a$10$ucxzI1qrqt.ZcE/vmQ82W.Wc5vnVE9rPmsc5S1vL7Gdr/VjFHWFR2', 0, 'STAFF', 0, 'NONE', 'sZqlfgjCLp'),
	('4611a093-efd9-4857-9cf2-657e01dab4f6', 'Hóc Môn, TPHCM, Vietnam', '2025-02-06 13:23:26', 'luongquocthai.thaigo.2003@gmail.com', 'Thái', 'Lương Quốc', '2025-02-07 15:47:29', '$2a$10$ucxzI1qrqt.ZcE/vmQ82W.Wc5vnVE9rPmsc5S1vL7Gdr/VjFHWFR2', 0, 'MANAGER', 0, 'NONE', 'WcVWFas21J'),
	('8f6e139d-3fc2-4be2-b6b6-57b543c7186e', '123 Main St, Anytown, USA', '2025-02-05 00:00:00', 'nguyenquocthai001005@gmail.com', 'Thái', 'Nguyễn Quốc', '2025-02-07 15:47:51', '$2a$10$ucxzI1qrqt.ZcE/vmQ82W.Wc5vnVE9rPmsc5S1vL7Gdr/VjFHWFR2', 0, 'GUEST', 0, 'NONE', 'FNxznfQW3g'),
	('cbafc696-ffd1-4870-8640-42d2e999783b', '123 Main St, Anytown, USA', '2025-02-05 00:00:00', '0tinking0@gmail.com', 'Tín', 'Võ Nhật', '2025-02-07 15:48:34', '$2a$10$ucxzI1qrqt.ZcE/vmQ82W.Wc5vnVE9rPmsc5S1vL7Gdr/VjFHWFR2', 0, 'CUSTOMER', 0, 'NONE', 'TUB5Qto6bo');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
