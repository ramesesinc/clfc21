create table mqpub (
	objid varchar(50) not null, 
	state varchar(32) not null, 
	dtfiled datetime not null,
	filedby_objid varchar(50) not null, 
	filedby_name varchar(150) not null, 
	message varchar(255) not null, 
	primary key (objid), 
	key ix_dtfiled (dtfiled), 
	key ix_filedby_objid (filedby_objid), 
	key ix_filedby_name (filedby_name)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

create table mqpubitem (
	objid varchar(50) not null,
	parentid varchar(50) not null, 
	reftype varchar(50) not null, 
	refid varchar(50) not null,  
	indexno int not null, 
	primary key (objid),
	key ix_parentid (parentid), 
	key ix_reftype (reftype), 
	key ix_refid (refid) 
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

create table mqpub_pending (
	objid varchar(50) not null,
	indexno int not null, 
	primary key (objid)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

create table mqpub_error (
	objid varchar(50) not null,
	parentid varchar(50) not null,
	message longtext not null, 
	primary key (objid),
	key ix_parentid (parentid) 
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


create table mqcon (
	objid varchar(50) not null, 
	dtfiled datetime not null,
	state varchar(32) not null, 
	primary key (objid), 
	key ix_dtfiled (dtfiled) 
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

create table mqconitem (
	objid varchar(50) not null,
	parentid varchar(50) not null, 
	reftype varchar(50) not null, 
	refid varchar(50) not null,  
	indexno int not null,
	content longtext not null,  
	primary key (objid),
	key ix_parentid (parentid), 
	key ix_reftype (reftype), 
	key ix_refid (refid) 
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
