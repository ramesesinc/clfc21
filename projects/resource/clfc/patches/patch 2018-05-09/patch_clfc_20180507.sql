CREATE TABLE `mqcon` (
  `objid` varchar(50) NOT NULL,
  `dtfiled` datetime NOT NULL,
  `state` varchar(32) NOT NULL,
  PRIMARY KEY (`objid`),
  KEY `ix_dtfiled` (`dtfiled`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `mqconitem` (
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `mqpub` (
  `objid` varchar(50) NOT NULL,
  `dtfiled` datetime NOT NULL,
  `state` varchar(32) NOT NULL,
  `filedby_objid` varchar(50) NOT NULL,
  `filedby_name` varchar(150) NOT NULL,
  `message` varchar(255) NOT NULL,
  PRIMARY KEY (`objid`),
  KEY `ix_dtfiled` (`dtfiled`),
  KEY `ix_filedby_objid` (`filedby_objid`),
  KEY `ix_filedby_name` (`filedby_name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `mqpub_error` (
  `objid` varchar(50) NOT NULL,
  `parentid` varchar(50) NOT NULL,
  `message` longtext NOT NULL,
  PRIMARY KEY (`objid`),
  KEY `ix_parentid` (`parentid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `mqpub_pending` (
  `objid` varchar(50) NOT NULL,
  `indexno` int(11) NOT NULL,
  PRIMARY KEY (`objid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `mqpubitem` (
  `objid` varchar(50) NOT NULL,
  `parentid` varchar(50) NOT NULL,
  `reftype` varchar(50) NOT NULL,
  `refid` varchar(50) NOT NULL,
  `indexno` int(11) NOT NULL,
  PRIMARY KEY (`objid`),
  KEY `ix_parentid` (`parentid`),
  KEY `ix_reftype` (`reftype`),
  KEY `ix_refid` (`refid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

