CREATE TABLE `borrower_header` (
  `objid` VARCHAR(11) NOT NULL DEFAULT '',
  `headerid` VARCHAR(50) DEFAULT NULL,
  PRIMARY KEY (`objid`)
) ENGINE=INNODB;

CREATE TABLE `borrower_header_merge` (
  `objid` varchar(11) NOT NULL,
  `headerid` varchar(11) DEFAULT NULL,
  PRIMARY KEY (`objid`),
  KEY `fk1_borrower_header_merge` (`headerid`),
  CONSTRAINT `fk1_borrower_header_merge` FOREIGN KEY (`headerid`) REFERENCES `borrower_header` (`objid`)
) ENGINE=InnoDB;

CREATE TABLE `loan_header` (
  `objid` varchar(11) NOT NULL,
  `headerid` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`objid`)
) ENGINE=InnoDB;

