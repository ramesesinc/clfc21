[getList]
select i.*
from (
	select objid
	from itemaccount
	where code like $P{searchtext}
	union
	select objid
	from itemaccount
	where title like $P{searchtext}
) q 
	inner join itemaccount i on q.objid = i.objid
order by i.dtcreated desc

[getListByState]
select i.*
from (
	select objid
	from itemaccount
	where code like $P{searchtext}
		and txnstate = $P{state}
	union
	select objid
	from itemaccount
	where title like $P{searchtext}
		and txnstate = $P{state}
) q 
	inner join itemaccount i on q.objid = i.objid
order by i.dtcreated desc

[getLookupList]
select i.*
from (
	select objid
	from itemaccount
	where code like $P{searchtext}
	union
	select objid
	from itemaccount
	where title like $P{searchtext}
) q 
	inner join itemaccount i on q.objid = i.objid
order by i.dtcreated desc

[getLookupListByState]
select i.*
from (
	select objid
	from itemaccount
	where code like $P{searchtext}
		and txnstate = $P{state}
	union
	select objid
	from itemaccount
	where title like $P{searchtext}
		and txnstate = $P{state}
) q 
	inner join itemaccount i on q.objid = i.objid
order by i.dtcreated desc