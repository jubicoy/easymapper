CREATE SCHEMA IF NOT EXISTS `CHAT_SAMPLE`;
DROP TABLE IF EXISTS `CHAT_SAMPLE`.`MESSAGE`;
DROP TABLE IF EXISTS `CHAT_SAMPLE`.`ROOM_MEMBERSHIP`;
DROP TABLE IF EXISTS `CHAT_SAMPLE`.`ROOM`;
DROP TABLE IF EXISTS `CHAT_SAMPLE`.`CHAT_USER`;
CREATE TABLE `CHAT_SAMPLE`.`CHAT_USER` (
    `id` int NOT NULL AUTO_INCREMENT,
    `role` varchar(100) NOT NULL,
    `name` varchar(255) NOT NULL,
    `deleted` boolean NOT NULL,
    PRIMARY KEY (`id`)
);
CREATE TABLE `CHAT_SAMPLE`.`ROOM` (
    `id` int NOT NULL AUTO_INCREMENT,
    `name` varchar(255) NOT NULL,
    `administrator_id` int,
    `created_by_id` int NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`administrator_id`) REFERENCES `CHAT_SAMPLE`.`CHAT_USER`(`id`),
    FOREIGN KEY (`created_by_id`) REFERENCES `CHAT_SAMPLE`.`CHAT_USER`(`id`)
);
CREATE TABLE `CHAT_SAMPLE`.`ROOM_MEMBERSHIP` (
    `id` int NOT NULL AUTO_INCREMENT,
    `room_id` int NOT NULL,
    `user_id` int NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`room_id`) REFERENCES `CHAT_SAMPLE`.`ROOM`(`id`),
    FOREIGN KEY (`user_id`) REFERENCES `CHAT_SAMPLE`.`CHAT_USER`(`id`)
);
CREATE TABLE `CHAT_SAMPLE`.`MESSAGE` (
    `id` int NOT NULL AUTO_INCREMENT,
    `text` varchar(255) NOT NULL,
    `author_id` int NOT NULL,
    `room_id` int NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`author_id`) REFERENCES `CHAT_SAMPLE`.`CHAT_USER`(`id`),
    FOREIGN KEY (`room_id`) REFERENCES `CHAT_SAMPLE`.`ROOM`(`id`)
);