[getList]
select p.*
from (
	select objid
	from postingheader
	where title like $P{searchtext}
	union
	select objid
	from postingheader
	where name like $P{searchtext}
) q inner join postingheader p on q.objid=p.objid
order by p.seqno, p.dtcreated desc 

[getListByCategory]
select p.*
from (
	select objid
	from postingheader
	where title like $P{searchtext}
		and category=$P{category}
	union
	select objid
	from postingheader
	where name like $P{searchtext}
		and category=$P{category}
) q inner join postingheader p on q.objid=p.objid
order by p.seqno, p.dtcreated desc 

[getLookupList]
select p.*
from (
	select objid
	from postingheader
	where title like $P{searchtext}
	union
	select objid
	from postingheader
	where name like $P{searchtext}
) q inner join postingheader p on q.objid=p.objid
order by p.seqno, p.dtcreated desc 

[getLookupListByCategory]
select p.*
from (
	select objid
	from postingheader
	where title like $P{searchtext}
		and category=$P{category}
	union
	select objid
	from postingheader
	where name like $P{searchtext}
		and category=$P{category}
) q inner join postingheader p on q.objid=p.objid
order by p.seqno, p.dtcreated desc 

[getFields]
select * from postingheader_factfield
where parentid=$P{objid}

[getFactListByCategory]
select f.*
from ils_sys_fact f
where f.objid in (select factid from ils_sys_fact_category where category=$P{category})

[getFieldListByFactid]
select f.*
from ils_sys_factfield f
where f.parentid=$P{factid}
order by f.seqno

[getFieldListSelectByCategory]
select concat(f.defaultvarname, "_", ff.fieldname) as title, f.objid as fact_objid, f.defaultvarname as fact_varname,
 	ff.objid as field_objid, ff.fieldname as field_name, ff.title as field_title, ff.handler as field_handler
from ils_sys_fact f
inner join ils_sys_factfield ff on f.objid=ff.parentid
where f.objid in (select factid from ils_sys_fact_category where category=$P{category})

[removeFields]
delete from postingheader_factfield where parentid=$P{objid}