[getList]
select a.* 
from (
	select a.code
	from loan_producttypeattribute a
	where a.code like $P{searchtext}
	union 
	select a.code
	from loan_producttypeattribute a
	where a.title like $P{searchtext}
) q inner join loan_producttypeattribute a on q.code=a.code
order by a.dtcreated desc

[getListByState]
select a.* 
from (
	select a.code
	from loan_producttypeattribute a
	where a.code like $P{searchtext}
		and a.txnstate = $P{state}
	union 
	select a.code
	from loan_producttypeattribute a
	where a.title like $P{searchtext}
		and a.txnstate = $P{state}
) q inner join loan_producttypeattribute a on q.code=a.code
order by a.dtcreated desc

[getLookupList]
select a.* 
from (
	select a.code
	from loan_producttypeattribute a
	where a.code like $P{searchtext}
	union 
	select a.code
	from loan_producttypeattribute a
	where a.title like $P{searchtext}
) q inner join loan_producttypeattribute a on q.code=a.code
order by a.dtcreated desc

[getLookupListByState]
select a.* 
from (
	select a.code
	from loan_producttypeattribute a
	where a.code like $P{searchtext}
		and a.txnstate = $P{state}
	union 
	select a.code
	from loan_producttypeattribute a
	where a.title like $P{searchtext}
		and a.txnstate = $P{state}
) q inner join loan_producttypeattribute a on q.code=a.code
order by a.dtcreated desc

[getLookupListByCategory]
select a.* 
from (
	select a.code
	from loan_producttypeattribute a
	where a.code like $P{searchtext}
		and a.category = $P{category}
	union 
	select a.code
	from loan_producttypeattribute a
	where a.title like $P{searchtext}
		and a.category = $P{category}
) q inner join loan_producttypeattribute a on q.code=a.code
order by a.dtcreated desc

[getLookupListByStateAndCategory]
select a.* 
from (
	select a.code
	from loan_producttypeattribute a
	where a.code like $P{searchtext}
		and a.txnstate = $P{state}
		and a.category = $P{category}
	union 
	select a.code
	from loan_producttypeattribute a
	where a.title like $P{searchtext}
		and a.txnstate = $P{state}
		and a.category = $P{category}
) q inner join loan_producttypeattribute a on q.code=a.code
order by a.dtcreated desc

[getDefaultAttributesByCategory]
select a.*
from loan_producttypeattribute a
where a.conditiontype = 'SYSTEM'
	and a.category = $P{category}