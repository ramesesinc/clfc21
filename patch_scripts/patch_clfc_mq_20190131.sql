CREATE TABLE IF NOT EXISTS `loanapp_feedback` (
  `objid` varchar(50) NOT NULL,
  `dtfiled` datetime DEFAULT NULL,
  `state` varchar(50) DEFAULT NULL,
  `branchid` varchar(100) DEFAULT NULL,
  `filedby_objid` varchar(50) DEFAULT NULL,
  `filedby_name` varchar(50) DEFAULT NULL,
  `refid` varchar(50) DEFAULT NULL,
  `data` mediumtext,
  PRIMARY KEY (`objid`),
  KEY `ix_dtfiled` (`dtfiled`),
  KEY `ix_state` (`state`),
  KEY `ix_filedbyid` (`filedby_objid`),
  KEY `ix_filedbyname` (`filedby_name`),
  KEY `ix_refid` (`refid`),
  KEY `ix_branchid` (`branchid`)
) ENGINE=InnoDB
;