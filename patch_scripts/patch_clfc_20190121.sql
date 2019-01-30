CREATE TABLE IF NOT EXISTS `loanapp_sendback` (
  `objid` varchar(50) NOT NULL,
  `dtfiled` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `filedby_objid` varchar(50) DEFAULT NULL,
  `filedby_name` varchar(255) DEFAULT NULL,
  `refid` varchar(50) DEFAULT NULL,
  `remarks` text,
  PRIMARY KEY (`objid`),
  KEY `fk_sendback_refid` (`refid`),
  KEY `ix_dtfiled` (`dtfiled`),
  KEY `ix_filedbyid` (`filedby_objid`),
  KEY `ix_filedbyname` (`filedby_name`),
  CONSTRAINT `fk_sendback_refid` FOREIGN KEY (`refid`) REFERENCES `loanapp` (`objid`)
) ENGINE=InnoDB
;