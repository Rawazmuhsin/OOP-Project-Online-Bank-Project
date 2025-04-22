-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: user_management
-- ------------------------------------------------------
-- Server version	8.0.41

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `accounts`
--

DROP TABLE IF EXISTS `accounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `accounts` (
  `username` varchar(50) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `account_id` int NOT NULL AUTO_INCREMENT,
  `password` varchar(255) NOT NULL,
  `balance` decimal(15,2) DEFAULT '0.00',
  `account_type` enum('Checking','Savings') NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `phone` varchar(20) NOT NULL,
  `account_number` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `accounts`
--

LOCK TABLES `accounts` WRITE;
/*!40000 ALTER TABLE `accounts` DISABLE KEYS */;
INSERT INTO `accounts` VALUES ('Rawaz.muhsin','Rawaz@example.com',10,'rawaz999',4900.00,'Checking','2025-04-18 11:10:15','07504559322',NULL),('azhy.a','azhy@example.com',11,'azhy123',0.00,'Checking','2025-04-18 13:15:56','047544496',NULL),('baxan.hamid','baxan@example.com',13,'baxan111',100.00,'Checking','2025-04-21 10:47:31','0750664666',NULL);
/*!40000 ALTER TABLE `accounts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admin`
--

DROP TABLE IF EXISTS `admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin` (
  `admin_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(100) NOT NULL,
  `first_name` varchar(50) DEFAULT NULL,
  `last_name` varchar(50) DEFAULT NULL,
  `role` varchar(20) DEFAULT 'manager',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `last_login` timestamp NULL DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`admin_id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin`
--

LOCK TABLES `admin` WRITE;
/*!40000 ALTER TABLE `admin` DISABLE KEYS */;
INSERT INTO `admin` VALUES (1,'Rawaz.muhsin','rawaz@muhsin.com','rawaz123','Rawaz','Muhsinn','manager','2025-04-22 15:32:06','2025-04-22 15:49:29',1);
/*!40000 ALTER TABLE `admin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transactions`
--

DROP TABLE IF EXISTS `transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transactions` (
  `transaction_id` int NOT NULL AUTO_INCREMENT,
  `account_id` int NOT NULL,
  `transaction_type` enum('Deposit','Withdrawal','Transfer') NOT NULL,
  `amount` decimal(15,2) NOT NULL,
  `transaction_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `description` varchar(255) DEFAULT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'PENDING',
  `approval_date` datetime DEFAULT NULL,
  `account_number` varchar(255) DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  PRIMARY KEY (`transaction_id`),
  KEY `account_id` (`account_id`),
  CONSTRAINT `transactions_ibfk_1` FOREIGN KEY (`account_id`) REFERENCES `accounts` (`account_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transactions`
--

LOCK TABLES `transactions` WRITE;
/*!40000 ALTER TABLE `transactions` DISABLE KEYS */;
INSERT INTO `transactions` VALUES (3,10,'Transfer',100.00,'2025-04-19 20:02:28','Transfer to account ID 11','APPROVED','2025-04-19 20:10:30',NULL,NULL),(4,10,'Transfer',100.00,'2025-04-19 20:06:23','Transfer to account ID 11','APPROVED','2025-04-19 20:09:12',NULL,NULL),(5,10,'Transfer',500.00,'2025-04-19 20:10:06','Transfer to account ID 11','APPROVED','2025-04-19 20:10:27',NULL,NULL),(6,10,'Transfer',250.00,'2025-04-19 20:25:52','Transfer to account ID 11','APPROVED','2025-04-19 20:26:07',NULL,NULL),(7,10,'Transfer',250.00,'2025-04-19 20:39:21','Transfer to account ID 12','APPROVED','2025-04-19 20:39:33',NULL,NULL),(8,10,'Transfer',250.00,'2025-04-20 17:07:02','Transfer to account ID 11','APPROVED',NULL,NULL,NULL),(9,10,'Deposit',500.00,'2025-04-20 17:20:26','yes','APPROVED','2025-04-20 17:20:39',NULL,NULL),(10,10,'Deposit',100.00,'2025-04-20 17:34:55','yess','APPROVED','2025-04-20 17:35:15',NULL,NULL),(11,10,'Withdrawal',100.00,'2025-04-20 17:42:47','yes','APPROVED','2025-04-20 17:43:00',NULL,NULL),(12,10,'Transfer',250.00,'2025-04-20 22:02:58','Transfer to account ID 11','APPROVED','2025-04-20 22:03:35',NULL,NULL),(13,10,'Deposit',100.00,'2025-04-21 10:49:21','yes','APPROVED','2025-04-21 10:49:58',NULL,NULL),(14,11,'Transfer',-100.00,'2025-04-21 19:03:33','Transfer to account ID 13','APPROVED',NULL,NULL,NULL),(15,13,'Transfer',100.00,'2025-04-21 19:03:33','Transfer from account ID 11','APPROVED',NULL,NULL,NULL),(16,10,'Withdrawal',250.00,'2025-04-21 19:10:13','','APPROVED','2025-04-21 19:10:28',NULL,NULL);
/*!40000 ALTER TABLE `transactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transfers`
--

DROP TABLE IF EXISTS `transfers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transfers` (
  `transfer_id` int NOT NULL AUTO_INCREMENT,
  `from_account_id` int NOT NULL,
  `to_account_id` int NOT NULL,
  `amount` decimal(15,2) NOT NULL,
  `transfer_date` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`transfer_id`),
  KEY `from_account_id` (`from_account_id`),
  KEY `to_account_id` (`to_account_id`),
  CONSTRAINT `transfers_ibfk_1` FOREIGN KEY (`from_account_id`) REFERENCES `accounts` (`account_id`),
  CONSTRAINT `transfers_ibfk_2` FOREIGN KEY (`to_account_id`) REFERENCES `accounts` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transfers`
--

LOCK TABLES `transfers` WRITE;
/*!40000 ALTER TABLE `transfers` DISABLE KEYS */;
/*!40000 ALTER TABLE `transfers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-04-22 19:05:18
