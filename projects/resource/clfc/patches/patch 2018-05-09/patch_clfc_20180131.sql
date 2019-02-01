create table loanapp_online ( 
	objid varchar(50) not null, 
	dtfiled datetime not null, 
	loanno varchar(25) not null, 
	version int not null, 
	clienttype varchar(20) not null, 
	marketedby varchar(150) null, 
	dtreleased date null, 
	primary key (objid), 
	unique key uix_loanno_version (loanno, version),  
	key ix_dtfiled (dtfiled), 
	key ix_loanno (loanno), 
	key ix_dtreleased (dtreleased) 
) ENGINE=InnoDB
; 

alter table loanapp_online add prevappid varchar(50) null;
create index ix_prevappid on loanapp_online (prevappid);

alter table loanapp_online add loancount int null; 
update loanapp_online set loancount=1 where loancount is null;
alter table loanapp_online modify loancount int not null; 
