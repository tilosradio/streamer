
CREATE TABLE `role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `parent_role_id` int(11) DEFAULT NULL,
  `name` varchar(10) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNIQ_57698A6AA44B56EA` (`parent_role_id`),
  CONSTRAINT `FK_57698A6AA44B56EA` FOREIGN KEY (`parent_role_id`) REFERENCES `role` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_id` int(11) DEFAULT NULL,
  `username` varchar(40) COLLATE utf8_unicode_ci NOT NULL,
  `password` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `email` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `salt` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_8D93D649D60322AC` (`role_id`),
  CONSTRAINT `FK_8D93D649D60322AC` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=71 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


CREATE TABLE `radioshow` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `definition` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `alias` varchar(60) COLLATE utf8_unicode_ci DEFAULT NULL,
  `banner` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `description` longtext COLLATE utf8_unicode_ci,
  `type` int(11) NOT NULL,
  `status` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=812 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


CREATE TABLE `api_audit` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `url` varchar(40) COLLATE utf8_unicode_ci NOT NULL,
  `postParams` varchar(500) COLLATE utf8_unicode_ci NOT NULL,
  `callDate` datetime NOT NULL,
  `method` varchar(10) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5391 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `url` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `url` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `type` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=270 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


CREATE TABLE `author` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `name` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `alias` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `photo` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `avatar` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  `introduction` longtext COLLATE utf8_unicode_ci NOT NULL,
  `email` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNIQ_BDAFD8C8A76ED395` (`user_id`),
  CONSTRAINT `FK_BDAFD8C8A76ED395` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1432 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `author_url` (
  `author_id` int(11) NOT NULL,
  `url_id` int(11) NOT NULL,
  PRIMARY KEY (`author_id`,`url_id`),
  UNIQUE KEY `UNIQ_1531912281CFDAE7` (`url_id`),
  KEY `IDX_15319122F675F31B` (`author_id`),
  CONSTRAINT `FK_1531912281CFDAE7` FOREIGN KEY (`url_id`) REFERENCES `url` (`id`),
  CONSTRAINT `FK_15319122F675F31B` FOREIGN KEY (`author_id`) REFERENCES `author` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


CREATE TABLE `textcontent` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `type` varchar(15) COLLATE utf8_unicode_ci NOT NULL,
  `format` varchar(15) COLLATE utf8_unicode_ci NOT NULL,
  `content` longtext COLLATE utf8_unicode_ci NOT NULL,
  `created` datetime NOT NULL,
  `modified` datetime NOT NULL,
  `author` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `alias` varchar(60) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6350 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


CREATE TABLE `episode` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `radioshow_id` int(11) DEFAULT NULL,
  `textcontent_id` int(11) DEFAULT NULL,
  `plannedFrom` datetime NOT NULL,
  `plannedTo` datetime NOT NULL,
  `realFrom` datetime NOT NULL,
  `realTo` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNIQ_DDAA1CDA3A333134` (`textcontent_id`),
  KEY `IDX_DDAA1CDA4E549204` (`radioshow_id`),
  CONSTRAINT `FK_DDAA1CDA3A333134` FOREIGN KEY (`textcontent_id`) REFERENCES `textcontent` (`id`),
  CONSTRAINT `FK_DDAA1CDA4E549204` FOREIGN KEY (`radioshow_id`) REFERENCES `radioshow` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=47011 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `bookmark` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `radioshow_id` int(11) DEFAULT NULL,
  `episode_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `start` datetime NOT NULL,
  `end` datetime NOT NULL,
  `modified` datetime NOT NULL,
  `title` varchar(160) COLLATE utf8_unicode_ci NOT NULL,
  `content` longtext COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_DA62921D4E549204` (`radioshow_id`),
  KEY `IDX_DA62921D362B62A0` (`episode_id`),
  KEY `IDX_DA62921DA76ED395` (`user_id`),
  CONSTRAINT `FK_DA62921D362B62A0` FOREIGN KEY (`episode_id`) REFERENCES `episode` (`id`),
  CONSTRAINT `FK_DA62921D4E549204` FOREIGN KEY (`radioshow_id`) REFERENCES `radioshow` (`id`),
  CONSTRAINT `FK_DA62921DA76ED395` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `change_password` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `token` varchar(40) COLLATE utf8_unicode_ci NOT NULL,
  `created` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNIQ_9A385A3BA76ED395` (`user_id`),
  CONSTRAINT `FK_9A385A3BA76ED395` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=226 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `contribution` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `radioshow_id` int(11) DEFAULT NULL,
  `author_id` int(11) DEFAULT NULL,
  `nick` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_EA351E154E549204` (`radioshow_id`),
  KEY `IDX_EA351E15F675F31B` (`author_id`),
  CONSTRAINT `FK_EA351E154E549204` FOREIGN KEY (`radioshow_id`) REFERENCES `radioshow` (`id`),
  CONSTRAINT `FK_EA351E15F675F31B` FOREIGN KEY (`author_id`) REFERENCES `author` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4044 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


CREATE TABLE `listener_stat` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` int(11) NOT NULL,
  `count` int(11) DEFAULT NULL,
  `date` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `mix` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `show_id` int(11) DEFAULT NULL,
  `author` varchar(160) COLLATE utf8_unicode_ci NOT NULL,
  `title` varchar(160) COLLATE utf8_unicode_ci NOT NULL,
  `file` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `date` date DEFAULT NULL,
  `type` int(11) NOT NULL,
  `category` int(11) NOT NULL,
  `content` longtext COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_55AFA881D0C1FC64` (`show_id`),
  CONSTRAINT `FK_55AFA881D0C1FC64` FOREIGN KEY (`show_id`) REFERENCES `radioshow` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1250 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `scheduling` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `radioshow_id` int(11) DEFAULT NULL,
  `weekType` smallint(6) NOT NULL,
  `base` date NOT NULL,
  `weekDay` smallint(6) NOT NULL,
  `hourFrom` smallint(6) NOT NULL,
  `minFrom` smallint(6) NOT NULL,
  `duration` smallint(6) NOT NULL,
  `validFrom` date NOT NULL,
  `validTo` date NOT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_FD931BF54E549204` (`radioshow_id`),
  CONSTRAINT `FK_FD931BF54E549204` FOREIGN KEY (`radioshow_id`) REFERENCES `radioshow` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1924 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `show_url` (
  `radioshow_id` int(11) NOT NULL,
  `url_id` int(11) NOT NULL,
  PRIMARY KEY (`radioshow_id`,`url_id`),
  UNIQUE KEY `UNIQ_D5AD498081CFDAE7` (`url_id`),
  KEY `IDX_D5AD49804E549204` (`radioshow_id`),
  CONSTRAINT `FK_D5AD49804E549204` FOREIGN KEY (`radioshow_id`) REFERENCES `radioshow` (`id`),
  CONSTRAINT `FK_D5AD498081CFDAE7` FOREIGN KEY (`url_id`) REFERENCES `url` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `tag` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `type` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=281 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


CREATE TABLE `tag_textcontent` (
  `textcontent_id` int(11) NOT NULL,
  `tag_id` int(11) NOT NULL,
  PRIMARY KEY (`textcontent_id`,`tag_id`),
  KEY `IDX_EB334393A333134` (`textcontent_id`),
  KEY `IDX_EB33439BAD26311` (`tag_id`),
  CONSTRAINT `FK_EB334393A333134` FOREIGN KEY (`textcontent_id`) REFERENCES `textcontent` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_EB33439BAD26311` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



