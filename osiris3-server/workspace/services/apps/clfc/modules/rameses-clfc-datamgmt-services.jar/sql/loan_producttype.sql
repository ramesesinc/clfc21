[getList]
select p.*
from (
	select code 
	from loan_producttype 
	where code like $P{searchtext}
	union
	select code
	from loan_producttype
	where title like $P{searchtext}
	union
	select code 
	from loan_producttype 
	where paymentschedule like $P{searchtext}
) q 
inner join loan_producttype p on q.code=p.code
order by p.dtcreated desc

[getListByState]
select p.*
from (
	select code 
	from loan_producttype 
	where code like $P{searchtext}
		and txnstate = $P{txnstate}
	union
	select code
	from loan_producttype
	where title like $P{searchtext}
		and txnstate = $P{txnstate}
	union
	select code 
	from loan_producttype 
	where paymentschedule like $P{searchtext}
		and txnstate = $P{txnstate}
) q 
inner join loan_producttype p on q.code=p.code
order by p.dtcreated desc

[getAttributesByCategory]
select * from loan_producttype_attribute
where parentid=$P{code}
	and category=$P{category}

[getFactListByCategory]
select f.*
from ils_sys_fact f
where f.objid in (select factid from ils_sys_fact_category where category=$P{category})

[getFieldListByFactid]
select f.*
from ils_sys_factfield f
where f.parentid=$P{factid}
order by f.seqno

[getFieldsByCategory]
select * from loan_producttype_factfield
where parentid=$P{objid}
	and category=$P{category}

[removeFieldsByCategory]
delete from loan_producttype_factfield where parentid=$P{objid} and category=$P{category}