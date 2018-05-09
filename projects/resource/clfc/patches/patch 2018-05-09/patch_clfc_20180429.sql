CREATE TABLE `profession` (
  `objid` varchar(100) NOT NULL,
  PRIMARY KEY (`objid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table loanapp_cireport (
	objid varchar(50) not null, 
	parentid varchar(50) not null, 
	refid varchar(50) null, 
	reftype varchar(32) not null, 
	remarks text null, 
	primary key (objid), 
	key ix_parentid (parentid), 
	key ix_refid (refid), 
	key ix_reftype (reftype), 
	CONSTRAINT `fk_loanapp_cireport_parentid` FOREIGN KEY (`parentid`) REFERENCES `loanapp` (`objid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

alter table loanapp_cireport 
	add dtfiled datetime null, 
	add filedby_objid varchar(50) null, 
	add filedby_name varchar(150) null,
	add key ix_dtfiled (dtfiled), 
	add key ix_filedby_objid (filedby_objid), 
	add key ix_filedby_name (filedby_name)
;

alter table loanapp_cireport change remarks evaluation text null;