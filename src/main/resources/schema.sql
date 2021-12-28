DROP TABLE IF EXISTS `example`;
CREATE TABLE `example`
(
    `id`      bigint(20)   NOT NULL AUTO_INCREMENT,
    `date`    date         NOT NULL,
    `time`    time         NOT NULL,
    `title`   varchar(255) NOT NULL,
    `content` varchar(512) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `example_summary`;
CREATE TABLE `example_summary`
(
    `id`       bigint(20)    NOT NULL AUTO_INCREMENT,
    `datetime` datetime      NOT NULL,
    `summary`  varchar(1024) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;
