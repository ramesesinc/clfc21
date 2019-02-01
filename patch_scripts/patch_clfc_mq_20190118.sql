CREATE TABLE IF NOT EXISTS `mq` (
  `objid` varchar(50) NOT NULL,
  `dtfiled` datetime NOT NULL,
  `state` varchar(32) NOT NULL,
  `filedby_objid` varchar(50) NOT NULL,
  `filedby_name` varchar(150) NOT NULL,
  `message` varchar(255) NOT NULL,
  PRIMARY KEY (`objid`),
  KEY `ix_dtfiled` (`dtfiled`)
) ENGINE=InnoDB
;
CREATE TABLE IF NOT EXISTS `mqitem` (
  `objid` varchar(50) NOT NULL,
  `parentid` varchar(50) NOT NULL,
  `reftype` varchar(50) NOT NULL,
  `refid` varchar(50) NOT NULL,
  `indexno` int(11) NOT NULL,
  `content` longtext NOT NULL,
  PRIMARY KEY (`objid`),
  KEY `ix_parentid` (`parentid`),
  KEY `ix_reftype` (`reftype`),
  KEY `ix_refid` (`refid`)
) ENGINE=InnoDB
;
CREATE TABLE IF NOT EXISTS `mq_forprocess` (
  `objid` varchar(50) NOT NULL,
  `indexno` int(11) NOT NULL,
  PRIMARY KEY (`objid`)
) ENGINE=InnoDB
;
CREATE TABLE IF NOT EXISTS `loanapp` (
  `objid` varchar(50) NOT NULL,
  `state` varchar(50) NOT NULL,
  `dtfiled` datetime NOT NULL,
  `filedby_objid` varchar(50) NOT NULL,
  `filedby_name` varchar(150) NOT NULL,
  `appid` varchar(50) NOT NULL,
  `appno` varchar(25) NOT NULL,
  `appdate` datetime NOT NULL,
  `apptype` varchar(25) NOT NULL,
  `borrower_objid` varchar(50) NOT NULL,
  `borrower_name` varchar(255) NOT NULL,
  `borrower_type` varchar(50) NOT NULL,
  `branch_objid` varchar(50) NOT NULL,
  `branch_name` varchar(255) NOT NULL,
  `dtapproved` datetime DEFAULT NULL,
  `approvedby_objid` varchar(50) DEFAULT NULL,
  `approvedby_name` varchar(150) DEFAULT NULL,
  PRIMARY KEY (`objid`),
  KEY `ix_state` (`state`),
  KEY `ix_dtfiled` (`dtfiled`),
  KEY `ix_filedby_objid` (`filedby_objid`),
  KEY `ix_filedby_name` (`filedby_name`),
  KEY `ix_appid` (`appid`),
  KEY `ix_appno` (`appno`),
  KEY `ix_appdate` (`appdate`),
  KEY `ix_borrower_objid` (`borrower_objid`),
  KEY `ix_borrower_name` (`borrower_name`),
  KEY `ix_branch_objid` (`branch_objid`),
  KEY `ix_branch_name` (`branch_name`),
  KEY `ix_dtapproved` (`dtapproved`),
  KEY `ix_approvedby_objid` (`approvedby_objid`),
  KEY `ix_approvedby_name` (`approvedby_name`)
) ENGINE=InnoDB
;
CREATE TABLE IF NOT EXISTS `loanapp_borrower` (
  `objid` varchar(50) NOT NULL,
  `parentid` varchar(50) NOT NULL,
  `reftype` varchar(25) NOT NULL,
  `data` longtext NOT NULL,
  PRIMARY KEY (`objid`),
  KEY `ix_parentid` (`parentid`)
) ENGINE=InnoDB
;
CREATE TABLE IF NOT EXISTS `loanapp_business` (
  `objid` varchar(50) NOT NULL,
  `parentid` varchar(50) NOT NULL,
  `data` longtext NOT NULL,
  PRIMARY KEY (`objid`),
  KEY `ix_parentid` (`parentid`)
) ENGINE=InnoDB
;
CREATE TABLE IF NOT EXISTS `loanapp_cireport` (
  `objid` varchar(50) NOT NULL,
  `parentid` varchar(50) NOT NULL,
  `reftype` varchar(32) NOT NULL,
  `data` longtext NOT NULL,
  PRIMARY KEY (`objid`),
  KEY `ix_parentid` (`parentid`),
  KEY `ix_reftype` (`reftype`)
) ENGINE=InnoDB
;
CREATE TABLE IF NOT EXISTS `loanapp_collateral` (
  `objid` varchar(50) NOT NULL,
  `parentid` varchar(50) NOT NULL,
  `reftype` varchar(32) NOT NULL,
  `data` longtext NOT NULL,
  PRIMARY KEY (`objid`),
  KEY `ix_parentid` (`parentid`),
  KEY `ix_reftype` (`reftype`)
) ENGINE=InnoDB
;
CREATE TABLE IF NOT EXISTS `loanapp_history` (
  `objid` varchar(50) NOT NULL,
  `parentid` varchar(50) DEFAULT NULL,
  `reftype` varchar(50) DEFAULT NULL,
  `data` longtext,
  PRIMARY KEY (`objid`),
  UNIQUE KEY `uix_objid` (`objid`),
  KEY `ix_parentid` (`parentid`),
  KEY `ix_reftype` (`reftype`),
  CONSTRAINT `fk_loanapp_history` FOREIGN KEY (`parentid`) REFERENCES `loanapp` (`objid`)
) ENGINE=InnoDB
;
CREATE TABLE IF NOT EXISTS `loanapp_otherinfo` (
  `objid` varchar(50) NOT NULL,
  `parentid` varchar(50) NOT NULL,
  `reftype` varchar(50) NOT NULL,
  `data` longtext NOT NULL,
  PRIMARY KEY (`objid`),
  KEY `ix_parentid` (`parentid`)
) ENGINE=InnoDB
;
CREATE TABLE IF NOT EXISTS `loanapp_recommendation` (
  `objid` varchar(50) NOT NULL,
  `data` longtext NOT NULL,
  PRIMARY KEY (`objid`)
) ENGINE=InnoDB
;
alter table loanapp_recommendation
add column refid varchar(50) default null after objid
;
alter table loanapp_recommendation
add column reftype varchar(100) default null after refid
;

