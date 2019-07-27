SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for red_packet
-- ----------------------------
DROP TABLE IF EXISTS `red_packet`;
CREATE TABLE `red_packet` (
  `id` int(12) NOT NULL AUTO_INCREMENT,
  `user_id` int(12) DEFAULT NULL,
  `amount` decimal(16,2) NOT NULL,
  `send_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `total` int(12) NOT NULL,
  `unit_amount` decimal(16,2) NOT NULL,
  `stock` int(12) NOT NULL,
  `version` int(12) NOT NULL DEFAULT '0',
  `note` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of red_packet
-- ----------------------------
BEGIN;
INSERT INTO `red_packet` VALUES (1, 1, 20000.00, '2019-07-26 22:37:38', 2000, 10.00, 0, 8000, '20万元金额，2万个小红包，每个10元');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
